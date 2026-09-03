package datn_gym.service;

import datn_gym.dto.response.TransactionResponse;
import datn_gym.config.PaymentProperties;
import datn_gym.entity.GymPackage;
import datn_gym.entity.Membership;
import datn_gym.entity.Promotion;
import datn_gym.entity.Transaction;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PackageHoldPolicyRepository;
import datn_gym.repository.PromotionRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MembershipRepository membershipRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final PaymentProperties paymentProperties;
    private final SaleService saleService;
    private final EmailService emailService;
    private final PackageHoldPolicyRepository holdPolicyRepository;

    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Page<TransactionResponse> getPendingTransactions(Pageable pageable) {
        return transactionRepository.findByStatus("PENDING", pageable)
                .map(this::toResponse);
    }

    /**
     * Chỉ tại bước xác nhận này thay đổi trên membership mới được áp dụng.
     */
    @Transactional
    public TransactionResponse confirmTransaction(Integer transactionId, String adminEmail) {
        Transaction tx = getPendingTransaction(transactionId);
        if ("MOMO".equals(tx.getPaymentMethod())) {
            throw new IllegalArgumentException(
                    "Giao dịch MoMo được xác nhận tự động bằng IPN, Admin không được duyệt thủ công.");
        }
        User admin = getAdmin(adminEmail);

        confirmPendingTransaction(tx, admin);
        return toResponse(tx);
    }

    @Transactional
    public void confirmMomoTransaction(
            String orderId,
            Long momoTransactionId,
            Integer resultCode,
            String message,
            Long amount) {
        Transaction tx = transactionRepository.findByGatewayOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch MoMo"));
        requireMatchingMomoTransaction(tx, amount);

        if ("CONFIRMED".equals(tx.getStatus())) {
            if (tx.getGatewayTransactionId() != null && momoTransactionId != null
                    && !tx.getGatewayTransactionId().equals(String.valueOf(momoTransactionId))) {
                throw new IllegalStateException("Giao dịch đã được xác nhận bởi một mã MoMo khác.");
            }
            return;
        }
        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Giao dịch MoMo không còn chờ thanh toán.");
        }

        tx.setGatewayTransactionId(momoTransactionId == null ? null : String.valueOf(momoTransactionId));
        tx.setGatewayResultCode(resultCode);
        tx.setGatewayMessage(limit(message, 500));
        tx.setPaidAt(LocalDateTime.now());
        confirmPendingTransaction(tx, null);
    }

    @Transactional
    public void failMomoTransaction(
            String orderId,
            Long momoTransactionId,
            Integer resultCode,
            String message,
            Long amount) {
        Transaction tx = transactionRepository.findByGatewayOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch MoMo"));
        requireMatchingMomoTransaction(tx, amount);
        if (!"PENDING".equals(tx.getStatus())) return;

        tx.setGatewayTransactionId(momoTransactionId == null ? null : String.valueOf(momoTransactionId));
        tx.setGatewayResultCode(resultCode);
        tx.setGatewayMessage(limit(message, 500));
        cancelPendingTransaction(tx, null);
    }

    @Transactional
    public void recordMomoQueryResult(String orderId, Integer resultCode, String message) {
        Transaction tx = transactionRepository.findByGatewayOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch MoMo"));
        if (!"PENDING".equals(tx.getStatus())) return;
        tx.setGatewayResultCode(resultCode);
        tx.setGatewayMessage(limit(message, 500));
        transactionRepository.save(tx);
    }

    private void confirmPendingTransaction(Transaction tx, User confirmedBy) {

        if (!Boolean.TRUE.equals(tx.getOperationApplied())) {
            applyPendingOperation(tx);
            tx.setOperationApplied(true);
        }
        tx.setStatus("CONFIRMED");
        tx.setConfirmedBy(confirmedBy);
        transactionRepository.save(tx);
        saleService.confirmRedemptionAndCommission(tx);
        queueMembershipConfirmationEmail(tx);
    }

    /**
     * Chụp dữ liệu email khi entity còn nằm trong transaction hiện tại. EmailService
     * nhận toàn giá trị thuần và gửi ở thread nền, vì vậy API không phải chờ SMTP.
     */
    private void queueMembershipConfirmationEmail(Transaction tx) {
        Membership membership = tx.getMembership();
        User member = membership.getUser();
        GymPackage confirmedPackage = tx.getRequestedPackage() != null
                ? tx.getRequestedPackage()
                : membership.getGymPackage();
        String recipientEmail = member.getEmail();
        String customerName = member.getFullName();
        Integer transactionId = tx.getId();
        String packageName = confirmedPackage.getName();
        String transactionType = tx.getType();
        BigDecimal amount = tx.getAmount();
        Integer termsVersion = tx.getTermsVersion();

        Runnable enqueueEmail = () -> {
            try {
                emailService.sendMembershipConfirmedEmail(
                        recipientEmail,
                        customerName,
                        transactionId,
                        packageName,
                        transactionType,
                        amount,
                        termsVersion);
            } catch (RuntimeException exception) {
                // Giao dịch đã được xác nhận; lỗi xếp hàng email không được làm
                // Admin hiểu nhầm rằng thao tác database đã thất bại.
                log.error("Không thể xếp email xác nhận giao dịch #{} vào hàng đợi", transactionId, exception);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enqueueEmail.run();
                }
            });
        } else {
            enqueueEmail.run();
        }
    }

    /**
     * Hủy RENEW/UPGRADE chỉ hủy yêu cầu thanh toán, không hủy gói đang dùng.
     * Với NEW, membership PENDING đi kèm mới được chuyển sang CANCELLED.
     */
    @Transactional
    public void cancelPendingMomoByMember(Integer transactionId, String memberEmail) {
        Transaction tx = transactionRepository
                .findByIdAndMembership_User_Email(transactionId, memberEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch của bạn."));
        if (!"MOMO".equals(tx.getPaymentMethod())) {
            throw new IllegalArgumentException("Giao dịch này không thanh toán qua MoMo.");
        }
        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể hủy giao dịch đang chờ thanh toán.");
        }
        tx.setGatewayMessage("Khách hàng đã hủy phiên thanh toán MoMo.");
        cancelPendingTransaction(tx, null);
    }

    @Transactional
    public TransactionResponse cancelTransaction(Integer transactionId, String adminEmail) {
        Transaction tx = getPendingTransaction(transactionId);
        User admin = getAdmin(adminEmail);

        cancelPendingTransaction(tx, admin);
        return toResponse(tx);
    }

    @Scheduled(cron = "${app.transaction.pending-expiration-cron:0 0 * * * *}")
    @Transactional
    public int expirePendingTransactions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(
                paymentProperties.effectivePendingExpirationHours());
        List<Transaction> expired = transactionRepository
                .findByStatusAndCreatedAtBefore("PENDING", cutoff);

        int cancelled = 0;
        for (Transaction tx : expired) {
            // Dữ liệu legacy có thể đã tác động membership trước khi duyệt.
            // Không tự động đảo ngược loại giao dịch đó.
            if (Boolean.TRUE.equals(tx.getOperationApplied())
                    && !"NEW".equals(tx.getType())) {
                continue;
            }
            cancelPendingTransaction(tx, null);
            cancelled++;
        }
        return cancelled;
    }

    private void cancelPendingTransaction(Transaction tx, User processedBy) {

        if (Boolean.TRUE.equals(tx.getOperationApplied()) && !"NEW".equals(tx.getType())) {
            throw new IllegalArgumentException(
                    "Giao dịch cũ này đã được áp dụng vào gói tập nên không thể tự động hủy. "
                            + "Hãy xác nhận giao dịch hoặc điều chỉnh membership thủ công.");
        }

        tx.setStatus("CANCELLED");
        tx.setConfirmedBy(processedBy);

        if ("NEW".equals(tx.getType())
                && ("PENDING".equals(tx.getMembership().getStatus())
                || Boolean.TRUE.equals(tx.getOperationApplied()))) {
            tx.getMembership().setStatus("CANCELLED");
            membershipRepository.save(tx.getMembership());
        }

        // Promotion được giữ chỗ lúc tạo giao dịch; trả lại khi giao dịch bị từ chối.
        if (tx.getPromotion() != null) {
            Promotion promotion = promotionRepository
                    .findByIdForUpdate(tx.getPromotion().getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Không tìm thấy khuyến mãi của giao dịch"));
            int currentUsage = promotion.getCurrentUsage() == null
                    ? 0
                    : promotion.getCurrentUsage();
            promotion.setCurrentUsage(Math.max(0, currentUsage - 1));
            promotionRepository.save(promotion);
        }

        transactionRepository.save(tx);
        saleService.releaseRedemption(tx);
    }

    private Transaction getPendingTransaction(Integer transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch"));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể xử lý giao dịch đang ở trạng thái PENDING!");
        }
        return tx;
    }

    private User getAdmin(String adminEmail) {
        return userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy admin"));
    }

    private void requireMatchingMomoTransaction(Transaction tx, Long amount) {
        if (!"MOMO".equals(tx.getPaymentMethod())) {
            throw new IllegalArgumentException("Giao dịch không thuộc phương thức MoMo.");
        }
        if (amount == null || tx.getAmount().compareTo(BigDecimal.valueOf(amount)) != 0) {
            throw new IllegalArgumentException("Số tiền callback MoMo không khớp giao dịch.");
        }
    }

    private void applyPendingOperation(Transaction tx) {
        Membership membership = tx.getMembership();
        Integer requestedDays = tx.getRequestedDurationDays();
        GymPackage requestedPackage = tx.getRequestedPackage();

        switch (tx.getType()) {
            case "NEW" -> {
                if (!"PENDING".equals(membership.getStatus())
                        || requestedDays == null || requestedDays <= 0
                        || requestedPackage == null) {
                    throw new IllegalStateException("Dữ liệu đăng ký mới không hợp lệ.");
                }
                LocalDate startDate = LocalDate.now();
                membership.setGymPackage(requestedPackage);
                membership.setPt(tx.getRequestedPt());
                membership.setDailyPrice(requestedPackage.getDailyPrice());
                membership.setDurationDays(requestedDays);
                membership.setStartDate(startDate);
                membership.setEndDate(startDate.plusDays(requestedDays));
                membership.setStatus("ACTIVE");
                refreshHoldPolicy(membership, requestedPackage, requestedDays);
            }
            case "RENEW" -> {
                requireActiveMembership(membership);
                if (requestedDays == null || requestedDays <= 0) {
                    throw new IllegalStateException("Số ngày gia hạn không hợp lệ.");
                }
                membership.setEndDate(membership.getEndDate().plusDays(requestedDays));
                membership.setDurationDays(membership.getDurationDays() + requestedDays);
                refreshHoldPolicy(membership, membership.getGymPackage(), membership.getDurationDays());
            }
            case "UPGRADE" -> {
                requireActiveMembership(membership);
                if (requestedPackage == null || requestedDays == null || requestedDays < 0) {
                    throw new IllegalStateException("Dữ liệu nâng cấp không hợp lệ.");
                }
                membership.setGymPackage(requestedPackage);
                membership.setPt(tx.getRequestedPt());
                membership.setDailyPrice(requestedPackage.getDailyPrice());
                if (requestedDays > 0) {
                    membership.setEndDate(membership.getEndDate().plusDays(requestedDays));
                    membership.setDurationDays(membership.getDurationDays() + requestedDays);
                }
                refreshHoldPolicy(membership, requestedPackage, membership.getDurationDays());
            }
            default -> throw new IllegalStateException("Loại giao dịch không được hỗ trợ.");
        }

        membershipRepository.save(membership);
    }

    private void refreshHoldPolicy(Membership membership, GymPackage gymPackage, int durationDays) {
        datn_gym.entity.PackageHoldPolicy policy = holdPolicyRepository
                .findApplicable(gymPackage.getId(), durationDays).orElse(null);
        membership.setHoldMaxTimes(policy == null ? 0 : policy.getMaxHoldTimes());
        membership.setHoldMaxDaysPerTime(policy == null ? 0 : policy.getMaxDaysPerHold());
        membership.setHoldMaxTotalDays(policy == null ? 0 : policy.getMaxTotalHoldDays());
    }

    private void requireActiveMembership(Membership membership) {
        if (!"ACTIVE".equals(membership.getStatus())
                || membership.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Gói tập không còn hoạt động; không thể áp dụng giao dịch này.");
        }
    }

    private TransactionResponse toResponse(Transaction tx) {
        GymPackage displayedPackage = tx.getRequestedPackage() != null
                ? tx.getRequestedPackage()
                : tx.getMembership().getGymPackage();

        return TransactionResponse.builder()
                .id(tx.getId())
                .membershipId(tx.getMembership().getId())
                .memberName(tx.getMembership().getUser().getFullName())
                .memberEmail(tx.getMembership().getUser().getEmail())
                .packageName(displayedPackage.getName())
                .originalAmount(tx.getOriginalAmount())
                .amount(tx.getAmount())
                .paymentMethod(tx.getPaymentMethod())
                .type(tx.getType())
                .status(tx.getStatus())
                .confirmedByName(tx.getConfirmedBy() != null
                        ? tx.getConfirmedBy().getFullName()
                        : null)
                .createdAt(tx.getCreatedAt())
                .promotionCode(tx.getPromotion() != null ? tx.getPromotion().getCode() : null)
                .referralCode(tx.getSaleCode() != null ? tx.getSaleCode().getCode() : null)
                .discountPercent(tx.getPromotion() != null
                        ? tx.getPromotion().getDiscountPercent() : tx.getCustomerDiscountPercent())
                .acceptedTerms(tx.getAcceptedTerms()).termsVersion(tx.getTermsVersion())
                .build();
    }

    private String limit(String value, int max) {
        return value == null ? null : value.substring(0, Math.min(value.length(), max));
    }
}
