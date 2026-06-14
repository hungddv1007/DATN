package datn_gym.service;

import datn_gym.dto.response.TransactionResponse;
import datn_gym.entity.Transaction;
import datn_gym.entity.User;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------
    // ADMIN: Xem tất cả giao dịch (có phân trang)
    // ----------------------------------------------------------------
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    // ----------------------------------------------------------------
    // ADMIN: Xem giao dịch đang chờ duyệt
    // ----------------------------------------------------------------
    public Page<TransactionResponse> getPendingTransactions(Pageable pageable) {
        return transactionRepository.findByStatus("PENDING", pageable)
                .map(this::toResponse);
    }

    // ----------------------------------------------------------------
    // ADMIN: Duyệt giao dịch (PENDING → CONFIRMED)
    // ----------------------------------------------------------------
    @Transactional
    public TransactionResponse confirmTransaction(Integer transactionId, String adminEmail) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch"));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể duyệt giao dịch đang ở trạng thái PENDING!");
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        tx.setStatus("CONFIRMED");
        tx.setConfirmedBy(admin);
        transactionRepository.save(tx);

        return toResponse(tx);
    }

    // ----------------------------------------------------------------
    // ADMIN: Hủy giao dịch (PENDING → CANCELLED)
    // ----------------------------------------------------------------
    @Transactional
    public TransactionResponse cancelTransaction(Integer transactionId, String adminEmail) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch"));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể hủy giao dịch đang ở trạng thái PENDING!");
        }

        tx.setStatus("CANCELLED");

        // Hủy luôn Membership tương ứng
        tx.getMembership().setStatus("CANCELLED");

        transactionRepository.save(tx);
        return toResponse(tx);
    }

    // ----------------------------------------------------------------
    // HELPER: Entity → Response DTO
    // ----------------------------------------------------------------
    private TransactionResponse toResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .membershipId(tx.getMembership().getId())
                .memberName(tx.getMembership().getUser().getFullName())
                .memberEmail(tx.getMembership().getUser().getEmail())
                .packageName(tx.getMembership().getGymPackage().getName())
                .originalAmount(tx.getOriginalAmount())
                .amount(tx.getAmount())
                .paymentMethod(tx.getPaymentMethod())
                .status(tx.getStatus())
                .confirmedByName(tx.getConfirmedBy() != null ? tx.getConfirmedBy().getFullName() : null)
                .createdAt(tx.getCreatedAt())
                .promotionCode(tx.getPromotion() != null ? tx.getPromotion().getCode() : null)
                .discountPercent(tx.getPromotion() != null ? tx.getPromotion().getDiscountPercent() : null)
                .build();
    }
}
