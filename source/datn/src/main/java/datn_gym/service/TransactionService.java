package datn_gym.service;

import datn_gym.dto.response.TransactionResponse;
import datn_gym.entity.GymPackage;
import datn_gym.entity.Membership;
import datn_gym.entity.Promotion;
import datn_gym.entity.Transaction;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PromotionRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MembershipRepository membershipRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

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
        User admin = getAdmin(adminEmail);

        if (!Boolean.TRUE.equals(tx.getOperationApplied())) {
            applyPendingOperation(tx);
            tx.setOperationApplied(true);
        }
        tx.setStatus("CONFIRMED");
        tx.setConfirmedBy(admin);
        transactionRepository.save(tx);

        return toResponse(tx);
    }

    /**
     * Hủy RENEW/UPGRADE chỉ hủy yêu cầu thanh toán, không hủy gói đang dùng.
     * Với NEW, membership PENDING đi kèm mới được chuyển sang CANCELLED.
     */
    @Transactional
    public TransactionResponse cancelTransaction(Integer transactionId, String adminEmail) {
        Transaction tx = getPendingTransaction(transactionId);
        User admin = getAdmin(adminEmail);

        if (Boolean.TRUE.equals(tx.getOperationApplied()) && !"NEW".equals(tx.getType())) {
            throw new IllegalArgumentException(
                    "Giao dịch cũ này đã được áp dụng vào gói tập nên không thể tự động hủy. "
                            + "Hãy xác nhận giao dịch hoặc điều chỉnh membership thủ công.");
        }

        tx.setStatus("CANCELLED");
        tx.setConfirmedBy(admin);

        if ("NEW".equals(tx.getType())
                && ("PENDING".equals(tx.getMembership().getStatus())
                || Boolean.TRUE.equals(tx.getOperationApplied()))) {
            tx.getMembership().setStatus("CANCELLED");
            membershipRepository.save(tx.getMembership());
        }

        // Promotion được giữ chỗ lúc tạo giao dịch; trả lại khi giao dịch bị từ chối.
        if (tx.getPromotion() != null) {
            Promotion promotion = tx.getPromotion();
            promotion.setCurrentUsage(Math.max(0, promotion.getCurrentUsage() - 1));
            promotionRepository.save(promotion);
        }

        transactionRepository.save(tx);
        return toResponse(tx);
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
            }
            case "RENEW" -> {
                requireActiveMembership(membership);
                if (requestedDays == null || requestedDays <= 0) {
                    throw new IllegalStateException("Số ngày gia hạn không hợp lệ.");
                }
                membership.setEndDate(membership.getEndDate().plusDays(requestedDays));
                membership.setDurationDays(membership.getDurationDays() + requestedDays);
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
            }
            default -> throw new IllegalStateException("Loại giao dịch không được hỗ trợ.");
        }

        membershipRepository.save(membership);
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
                .discountPercent(tx.getPromotion() != null
                        ? tx.getPromotion().getDiscountPercent()
                        : null)
                .build();
    }
}
