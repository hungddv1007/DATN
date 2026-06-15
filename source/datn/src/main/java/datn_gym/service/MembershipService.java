package datn_gym.service;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GymPackageRepository gymPackageRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final TransactionRepository transactionRepository;

    // ----------------------------------------------------------------
    // MEMBER: Đăng ký gói tập
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse registerPackage(String memberEmail, MembershipRequest request) {
        User member = getUserByEmail(memberEmail);

        // 1. Kiểm tra đã có gói ACTIVE chưa
        membershipRepository.findByUser_IdAndStatus(member.getId(), "ACTIVE")
                .ifPresent(m -> {
                    throw new IllegalArgumentException(
                            "Bạn đang có gói tập đang hoạt động. Vui lòng chờ hết hạn hoặc hủy gói hiện tại!");
                });

        // 2. Lấy thông tin gói tập
        GymPackage gymPackage = gymPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));

        if (Boolean.FALSE.equals(gymPackage.getIsActive())) {
            throw new IllegalArgumentException("Gói tập này hiện đã ngừng cung cấp.");
        }

        // 3. Xử lý PT (nếu gói có PT)
        User pt = null;
        if (Boolean.TRUE.equals(gymPackage.getCanChoosePt()) && request.getPtId() != null) {
            pt = userRepository.findById(request.getPtId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy PT"));
            if (!"PT".equals(pt.getRole().getName())) {
                throw new IllegalArgumentException("Người dùng được chọn không phải là PT!");
            }
        }

        // 4. Tính tiền (áp dụng khuyến mãi nếu có)
        BigDecimal originalAmount = gymPackage.getPrice();
        BigDecimal finalAmount = originalAmount;
        Promotion promotion = null;
        Integer discountPercent = null;

        if (request.getPromotionCode() != null && !request.getPromotionCode().isBlank()) {
            promotion = promotionRepository.findValidPromotion(
                    request.getPromotionCode(),
                    LocalDate.now(),
                    gymPackage.getId()
            ).orElseThrow(() -> new IllegalArgumentException(
                    "Mã khuyến mãi không hợp lệ hoặc đã hết hạn!"));

            discountPercent = promotion.getDiscountPercent();
            finalAmount = originalAmount.subtract(
                    originalAmount.multiply(BigDecimal.valueOf(discountPercent)).divide(BigDecimal.valueOf(100))
            );

            // Tăng số lần sử dụng mã KM
            promotion.setCurrentUsage(promotion.getCurrentUsage() + 1);
            promotionRepository.save(promotion);
        }

        // 5. Tạo Membership
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(gymPackage.getDurationDays());

        Membership membership = Membership.builder()
                .user(member)
                .gymPackage(gymPackage)
                .pt(pt)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .build();

        membershipRepository.save(membership);

        // 6. Tạo Transaction (trạng thái PENDING, chờ Admin duyệt)
        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(promotion)
                .originalAmount(originalAmount)
                .amount(finalAmount)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK")
                .status("PENDING")
                .build();

        transactionRepository.save(transaction);

        // 7. Trả kết quả
        return toResponse(membership, transaction, promotion);
    }

    // ----------------------------------------------------------------
    // MEMBER: Xem gói tập hiện tại
    // ----------------------------------------------------------------
    public MembershipResponse getMyCurrentMembership(String email) {
        User user = getUserByEmail(email);
        Membership membership = membershipRepository.findByUser_IdAndStatus(user.getId(), "ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bạn chưa đăng ký gói tập nào"));

        // Lấy transaction mới nhất
        List<Transaction> transactions = transactionRepository.findByMembership_Id(membership.getId());
        Transaction latestTx = transactions.isEmpty() ? null : transactions.get(0);

        return toResponse(membership, latestTx, latestTx != null ? latestTx.getPromotion() : null);
    }

    // ----------------------------------------------------------------
    // MEMBER: Xem lịch sử đăng ký
    // ----------------------------------------------------------------
    public List<MembershipResponse> getMyMembershipHistory(String email) {
        User user = getUserByEmail(email);
        return membershipRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(m -> {
                    List<Transaction> txList = transactionRepository.findByMembership_Id(m.getId());
                    Transaction tx = txList.isEmpty() ? null : txList.get(0);
                    return toResponse(m, tx, tx != null ? tx.getPromotion() : null);
                })
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // HELPER
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private MembershipResponse toResponse(Membership m, Transaction tx, Promotion promo) {
        return MembershipResponse.builder()
                .id(m.getId())
                .packageName(m.getGymPackage().getName())
                .packagePrice(m.getGymPackage().getPrice())
                .durationDays(m.getGymPackage().getDurationDays())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .ptName(m.getPt() != null ? m.getPt().getFullName() : null)
                .createdAt(m.getCreatedAt())
                .transactionId(tx != null ? tx.getId() : null)
                .originalAmount(tx != null ? tx.getOriginalAmount() : null)
                .finalAmount(tx != null ? tx.getAmount() : null)
                .paymentMethod(tx != null ? tx.getPaymentMethod() : null)
                .transactionStatus(tx != null ? tx.getStatus() : null)
                .promotionCode(promo != null ? promo.getCode() : null)
                .discountPercent(promo != null ? promo.getDiscountPercent() : null)
                .build();
    }
}
