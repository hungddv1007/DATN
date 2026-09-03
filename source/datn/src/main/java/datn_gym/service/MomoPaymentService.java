package datn_gym.service;

import datn_gym.config.MomoProperties;
import datn_gym.dto.request.MomoIpnRequest;
import datn_gym.dto.response.MomoPaymentResponse;
import datn_gym.entity.Transaction;
import datn_gym.payment.momo.MomoClient;
import datn_gym.payment.momo.MomoGatewayResponse;
import datn_gym.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoPaymentService {

    private static final long MOMO_MIN_AMOUNT = 1_000L;
    private static final long MOMO_MAX_AMOUNT = 50_000_000L;

    private final TransactionRepository transactionRepository;
    private final MomoProperties properties;
    private final MomoClient momoClient;
    private final TransactionService transactionService;

    @Transactional
    public MomoPaymentResponse initiate(Integer transactionId, String memberEmail) {
        properties.requireConfigured();
        Transaction transaction = getOwnedTransaction(transactionId, memberEmail);
        requirePayableMomoTransaction(transaction);

        if (hasReusableCheckout(transaction)) {
            return toResponse(transaction);
        }
        if (transaction.getPaymentExpiresAt() != null
                && !transaction.getPaymentExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE,
                    "Mã QR MoMo đã hết hạn. Vui lòng tạo lại giao dịch.");
        }

        long amount = toMomoAmount(transaction.getAmount());
        if (transaction.getGatewayOrderId() == null) {
            transaction.setGatewayOrderId("GYMPRO_TX_" + transaction.getId());
        }
        if (transaction.getGatewayRequestId() == null) {
            transaction.setGatewayRequestId("MOMO_TX_" + transaction.getId());
        }
        transaction.setPaymentExpiresAt(LocalDateTime.now()
                .plusMinutes(properties.effectiveExpirationMinutes()));
        transactionRepository.saveAndFlush(transaction);

        String redirectUrl = appendTransactionId(properties.redirectUrl().trim(), transaction.getId());
        String orderInfo = "Thanh toan goi tap GymPro #" + transaction.getId();
        MomoGatewayResponse gateway = momoClient.createPayment(
                transaction.getGatewayOrderId(),
                transaction.getGatewayRequestId(),
                amount,
                orderInfo,
                redirectUrl);

        validateGatewayIdentity(transaction, gateway);
        transaction.setGatewayResultCode(gateway.getResultCode());
        transaction.setGatewayMessage(limit(gateway.getMessage(), 500));
        if (!Integer.valueOf(0).equals(gateway.getResultCode())) {
            throw new IllegalStateException("MoMo từ chối tạo thanh toán: "
                    + safeMessage(gateway.getMessage(), gateway.getResultCode()));
        }
        transaction.setGatewayPayUrl(limit(gateway.getPayUrl(), 1000));
        transaction.setGatewayDeeplink(limit(gateway.getDeeplink(), 1000));
        transaction.setGatewayQrContent(limit(gateway.getQrCodeUrl(), 2000));
        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    public MomoPaymentResponse getStatus(Integer transactionId, String memberEmail) {
        return toResponse(getOwnedTransaction(transactionId, memberEmail));
    }

    public MomoPaymentResponse refreshStatus(Integer transactionId, String memberEmail) {
        properties.requireConfigured();
        Transaction transaction = getOwnedTransaction(transactionId, memberEmail);
        if (!"MOMO".equals(transaction.getPaymentMethod())) {
            throw new IllegalArgumentException("Giao dịch này không thanh toán qua MoMo.");
        }
        if (!"PENDING".equals(transaction.getStatus())
                || transaction.getGatewayOrderId() == null) {
            return toResponse(transaction);
        }

        String queryRequestId = "QUERY_" + UUID.randomUUID().toString().replace("-", "");
        MomoGatewayResponse gateway = momoClient.queryPayment(
                transaction.getGatewayOrderId(), queryRequestId);
        validateQueryIdentity(transaction, gateway);

        if (Integer.valueOf(0).equals(gateway.getResultCode())) {
            transactionService.confirmMomoTransaction(
                    transaction.getGatewayOrderId(),
                    gateway.getTransId(),
                    gateway.getResultCode(),
                    gateway.getMessage(),
                    gateway.getAmount());
        } else {
            transactionService.recordMomoQueryResult(
                    transaction.getGatewayOrderId(),
                    gateway.getResultCode(),
                    gateway.getMessage());
        }
        return toResponse(getOwnedTransaction(transactionId, memberEmail));
    }

    public void cancel(Integer transactionId, String memberEmail) {
        transactionService.cancelPendingMomoByMember(transactionId, memberEmail);
    }

    public void handleIpn(MomoIpnRequest request) {
        properties.requireConfigured();
        if (request == null || !momoClient.isValidIpnSignature(request)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chữ ký IPN MoMo không hợp lệ.");
        }
        if (!properties.partnerCode().trim().equals(request.getPartnerCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sai mã đối tác MoMo.");
        }

        Transaction transaction = transactionRepository.findByGatewayOrderId(request.getOrderId())
                .orElse(null);
        if (transaction == null) {
            // Không cập nhật bất kỳ dữ liệu nào nhưng vẫn xác nhận đã nhận callback để
            // MoMo không gửi lặp vô hạn cho một order không thuộc hệ thống này.
            log.warn("Nhận IPN MoMo cho order không tồn tại: {}", request.getOrderId());
            return;
        }
        validateIpnIdentity(transaction, request);

        if (Integer.valueOf(0).equals(request.getResultCode())) {
            transactionService.confirmMomoTransaction(
                    request.getOrderId(), request.getTransId(), request.getResultCode(),
                    request.getMessage(), request.getAmount());
        } else {
            transactionService.failMomoTransaction(
                    request.getOrderId(), request.getTransId(), request.getResultCode(),
                    request.getMessage(), request.getAmount());
        }
    }

    private Transaction getOwnedTransaction(Integer transactionId, String memberEmail) {
        return transactionRepository.findByIdAndMembership_User_Email(transactionId, memberEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch của bạn."));
    }

    private void requirePayableMomoTransaction(Transaction transaction) {
        if (!"MOMO".equals(transaction.getPaymentMethod())) {
            throw new IllegalArgumentException("Giao dịch này không thanh toán qua MoMo.");
        }
        if (!"PENDING".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("Giao dịch không còn ở trạng thái chờ thanh toán.");
        }
    }

    private boolean hasReusableCheckout(Transaction transaction) {
        return transaction.getPaymentExpiresAt() != null
                && transaction.getPaymentExpiresAt().isAfter(LocalDateTime.now())
                && (transaction.getGatewayQrContent() != null
                    || transaction.getGatewayPayUrl() != null);
    }

    private long toMomoAmount(BigDecimal amount) {
        if (amount == null || amount.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Số tiền MoMo phải là số nguyên VND.");
        }
        long value = amount.longValueExact();
        if (value < MOMO_MIN_AMOUNT || value > MOMO_MAX_AMOUNT) {
            throw new IllegalArgumentException(
                    "MoMo chỉ hỗ trợ giao dịch từ 1.000đ đến 50.000.000đ.");
        }
        return value;
    }

    private void validateGatewayIdentity(Transaction transaction, MomoGatewayResponse response) {
        if (!properties.partnerCode().trim().equals(response.getPartnerCode())
                || !transaction.getGatewayOrderId().equals(response.getOrderId())
                || !transaction.getGatewayRequestId().equals(response.getRequestId())
                || response.getAmount() == null
                || transaction.getAmount().compareTo(BigDecimal.valueOf(response.getAmount())) != 0) {
            throw new IllegalStateException("Phản hồi tạo thanh toán MoMo không khớp giao dịch.");
        }
    }

    private void validateQueryIdentity(Transaction transaction, MomoGatewayResponse response) {
        if (!properties.partnerCode().trim().equals(response.getPartnerCode())
                || !transaction.getGatewayOrderId().equals(response.getOrderId())) {
            throw new IllegalStateException("Phản hồi truy vấn MoMo không khớp giao dịch.");
        }
        if (Integer.valueOf(0).equals(response.getResultCode())
                && (response.getAmount() == null
                    || transaction.getAmount().compareTo(BigDecimal.valueOf(response.getAmount())) != 0)) {
            throw new IllegalStateException("Số tiền MoMo trả về không khớp giao dịch.");
        }
    }

    private void validateIpnIdentity(Transaction transaction, MomoIpnRequest request) {
        if (!"MOMO".equals(transaction.getPaymentMethod())
                || !transaction.getGatewayRequestId().equals(request.getRequestId())
                || request.getAmount() == null
                || transaction.getAmount().compareTo(BigDecimal.valueOf(request.getAmount())) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thông tin IPN MoMo không khớp giao dịch.");
        }
    }

    private MomoPaymentResponse toResponse(Transaction transaction) {
        return MomoPaymentResponse.builder()
                .transactionId(transaction.getId())
                .orderId(transaction.getGatewayOrderId())
                .amount(transaction.getAmount())
                .transactionStatus(transaction.getStatus())
                .payUrl(transaction.getGatewayPayUrl())
                .deeplink(transaction.getGatewayDeeplink())
                .qrCode(transaction.getGatewayQrContent())
                .resultCode(transaction.getGatewayResultCode())
                .message(transaction.getGatewayMessage())
                .expiresAt(transaction.getPaymentExpiresAt())
                .paidAt(transaction.getPaidAt())
                .build();
    }

    private String appendTransactionId(String url, Integer transactionId) {
        return url + (url.contains("?") ? "&" : "?") + "transactionId=" + transactionId;
    }

    private String safeMessage(String message, Integer code) {
        return message == null || message.isBlank() ? "mã lỗi " + code : message;
    }

    private String limit(String value, int max) {
        return value == null ? null : value.substring(0, Math.min(value.length(), max));
    }
}
