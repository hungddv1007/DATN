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
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GymPackageRepository gymPackageRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final TransactionRepository transactionRepository;
    private final PackageDiscountRepository discountRepository;

    // ----------------------------------------------------------------
    // 1. REGISTER NEW (Đăng ký mới)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse registerPackage(String memberEmail, MembershipRequest request) {
        User member = getUserByEmail(memberEmail);

        // Kiểm tra chưa có gói ACTIVE hoặc PAUSED
        if (membershipRepository.findByUser_IdAndStatus(member.getId(), "ACTIVE").isPresent() ||
            membershipRepository.findByUser_IdAndStatus(member.getId(), "PAUSED").isPresent()) {
            throw new IllegalArgumentException("Bạn đang có gói tập đang hoạt động hoặc bảo lưu. Vui lòng gia hạn hoặc nâng cấp!");
        }

        GymPackage gymPackage = getActivePackage(request.getPackageId());

        if (request.getDurationDays() == null || request.getDurationDays() < gymPackage.getMinDays()) {
            throw new IllegalArgumentException("Gói tập này yêu cầu đăng ký tối thiểu " + gymPackage.getMinDays() + " ngày.");
        }

        User pt = validatePtSelection(gymPackage, request.getPtId());

        // Tính tiền (gốc, chiết khấu, mã KM)
        PricingResult pricing = calculatePricing(gymPackage, request.getDurationDays(), request.getPromotionCode());

        // Tạo Membership
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getDurationDays());

        Membership membership = Membership.builder()
                .user(member)
                .gymPackage(gymPackage)
                .pt(pt)
                .startDate(startDate)
                .endDate(endDate)
                .durationDays(request.getDurationDays())
                .dailyPrice(gymPackage.getDailyPrice())
                .status("ACTIVE")
                .build();
        membershipRepository.save(membership);

        // Tạo Transaction
        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(pricing.promotion)
                .originalAmount(pricing.grossAmount)
                .amount(pricing.finalAmount)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK")
                .status("PENDING")
                .type("NEW")
                .build();
        transactionRepository.save(transaction);

        return toResponse(membership, transaction, pricing.promotion, pricing.discountPercent);
    }

    // ----------------------------------------------------------------
    // 2. RENEW (Gia hạn cùng gói)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse renewPackage(String memberEmail, MembershipRequest request) {
        User member = getUserByEmail(memberEmail);
        Membership membership = getActiveOrPausedMembership(member.getId());

        if (request.getDurationDays() == null || request.getDurationDays() < membership.getGymPackage().getMinDays()) {
            throw new IllegalArgumentException("Yêu cầu gia hạn tối thiểu " + membership.getGymPackage().getMinDays() + " ngày.");
        }

        PricingResult pricing = calculatePricing(membership.getGymPackage(), request.getDurationDays(), request.getPromotionCode());

        membership.setEndDate(membership.getEndDate().plusDays(request.getDurationDays()));
        membership.setDurationDays(membership.getDurationDays() + request.getDurationDays());
        membershipRepository.save(membership);

        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(pricing.promotion)
                .originalAmount(pricing.grossAmount)
                .amount(pricing.finalAmount)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK")
                .status("PENDING")
                .type("RENEW")
                .build();
        transactionRepository.save(transaction);

        return toResponse(membership, transaction, pricing.promotion, pricing.discountPercent);
    }

    // ----------------------------------------------------------------
    // 3. UPGRADE (Nâng cấp tại chỗ)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse upgradePackage(String memberEmail, MembershipRequest request) {
        User member = getUserByEmail(memberEmail);
        Membership membership = getActiveOrPausedMembership(member.getId());

        GymPackage newPackage = getActivePackage(request.getNewPackageId());

        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
        if (remainingDays <= 0) {
            throw new IllegalArgumentException("Gói tập đã hết hạn, không thể nâng cấp.");
        }

        if (remainingDays < newPackage.getMinDays()) {
            throw new IllegalArgumentException("Số ngày còn lại (" + remainingDays + ") không đủ điều kiện tối thiểu (" + newPackage.getMinDays() + ") của gói mới. Vui lòng chọn nâng cấp kèm gia hạn!");
        }

        User pt = validatePtSelection(newPackage, request.getPtId());

        // Tính PRORATION
        BigDecimal credit = membership.getDailyPrice().multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal costNew = newPackage.getDailyPrice().multiply(BigDecimal.valueOf(remainingDays));
        
        BigDecimal upgradeCost = costNew.subtract(credit);
        if (upgradeCost.compareTo(BigDecimal.ZERO) < 0) {
            upgradeCost = BigDecimal.ZERO; // Không hoàn tiền
        }

        // Cập nhật membership
        membership.setGymPackage(newPackage);
        membership.setDailyPrice(newPackage.getDailyPrice());
        if (pt != null) membership.setPt(pt);
        membershipRepository.save(membership);

        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(null)
                .originalAmount(costNew)
                .amount(upgradeCost)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK")
                .status("PENDING")
                .type("UPGRADE")
                .build();
        transactionRepository.save(transaction);

        return toResponse(membership, transaction, null, null);
    }

    // ----------------------------------------------------------------
    // 4. UPGRADE & RENEW (Nâng cấp kèm gia hạn)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse upgradeAndRenewPackage(String memberEmail, MembershipRequest request) {
        User member = getUserByEmail(memberEmail);
        Membership membership = getActiveOrPausedMembership(member.getId());

        GymPackage newPackage = getActivePackage(request.getNewPackageId());

        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
        if (remainingDays < 0) remainingDays = 0;

        int addedDays = request.getDurationDays() != null ? request.getDurationDays() : 0;
        long totalNewDays = remainingDays + addedDays;

        if (totalNewDays < newPackage.getMinDays()) {
            throw new IllegalArgumentException("Tổng số ngày (" + totalNewDays + ") không đủ điều kiện tối thiểu (" + newPackage.getMinDays() + ") của gói mới.");
        }

        User pt = validatePtSelection(newPackage, request.getPtId());

        // Tính PRORATION
        BigDecimal credit = membership.getDailyPrice().multiply(BigDecimal.valueOf(remainingDays));
        
        PricingResult pricing = calculatePricing(newPackage, (int) totalNewDays, request.getPromotionCode());
        
        BigDecimal upgradeCost = pricing.finalAmount.subtract(credit);
        if (upgradeCost.compareTo(BigDecimal.ZERO) < 0) {
            upgradeCost = BigDecimal.ZERO; // Không hoàn tiền
        }

        membership.setGymPackage(newPackage);
        membership.setDailyPrice(newPackage.getDailyPrice());
        membership.setEndDate(LocalDate.now().plusDays(totalNewDays));
        membership.setDurationDays((int) totalNewDays);
        if (pt != null) membership.setPt(pt);
        membershipRepository.save(membership);

        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(pricing.promotion)
                .originalAmount(pricing.grossAmount)
                .amount(upgradeCost)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK")
                .status("PENDING")
                .type("UPGRADE")
                .build();
        transactionRepository.save(transaction);

        return toResponse(membership, transaction, pricing.promotion, pricing.discountPercent);
    }

    // ----------------------------------------------------------------
    // 5. PAUSE (Bảo lưu)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse pauseMembership(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        Membership membership = membershipRepository.findByUser_IdAndStatus(member.getId(), "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập đang hoạt động."));

        GymPackage gymPackage = membership.getGymPackage();
        if (gymPackage.getMaxHoldTimes() == null || gymPackage.getMaxHoldTimes() <= 0) {
            throw new IllegalArgumentException("Gói tập này không hỗ trợ bảo lưu.");
        }
        if (membership.getHoldCount() >= gymPackage.getMaxHoldTimes()) {
            throw new IllegalArgumentException("Bạn đã hết lượt bảo lưu cho gói tập này.");
        }

        membership.setStatus("PAUSED");
        membership.setPausedAt(LocalDate.now());
        membership.setHoldCount(membership.getHoldCount() + 1);
        membershipRepository.save(membership);

        return toResponse(membership, null, null, null);
    }

    // ----------------------------------------------------------------
    // 6. RESUME (Hủy bảo lưu)
    // ----------------------------------------------------------------
    @Transactional
    public MembershipResponse resumeMembership(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        Membership membership = membershipRepository.findByUser_IdAndStatus(member.getId(), "PAUSED")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập đang bảo lưu."));

        GymPackage gymPackage = membership.getGymPackage();
        long holdDays = ChronoUnit.DAYS.between(membership.getPausedAt(), LocalDate.now());
        
        // Tính % trả lại ngày
        int percent = gymPackage.getHoldReturnPercent() != null ? gymPackage.getHoldReturnPercent() : 0;
        long returnDays = (long) Math.floor(holdDays * percent / 100.0);

        membership.setEndDate(membership.getEndDate().plusDays(returnDays));
        membership.setStatus("ACTIVE");
        membership.setPausedAt(null);
        membership.setTotalHoldDays(membership.getTotalHoldDays() + (int) holdDays);
        membershipRepository.save(membership);

        return toResponse(membership, null, null, null);
    }

    // ----------------------------------------------------------------
    // 7. CANCEL (Hủy gói)
    // ----------------------------------------------------------------
    @Transactional
    public void cancelMembership(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        Membership membership = getActiveOrPausedMembership(member.getId());
        membership.setStatus("CANCELLED");
        membershipRepository.save(membership);
    }

    // ----------------------------------------------------------------
    // PREVIEW APIs
    // ----------------------------------------------------------------
    public Map<String, Object> previewUpgrade(String memberEmail, Integer newPackageId) {
        User member = getUserByEmail(memberEmail);
        Membership membership = getActiveOrPausedMembership(member.getId());
        GymPackage newPackage = getActivePackage(newPackageId);

        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
        if (remainingDays <= 0) remainingDays = 0;

        BigDecimal credit = membership.getDailyPrice().multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal costNew = newPackage.getDailyPrice().multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal upgradeCost = costNew.subtract(credit);
        if (upgradeCost.compareTo(BigDecimal.ZERO) < 0) upgradeCost = BigDecimal.ZERO;

        Map<String, Object> res = new HashMap<>();
        res.put("remainingDays", remainingDays);
        res.put("creditAmount", credit);
        res.put("newCostAmount", costNew);
        res.put("upgradeCost", upgradeCost);
        return res;
    }

    public Map<String, Object> previewRenew(String memberEmail, Integer packageId, Integer days) {
        GymPackage gymPackage = getActivePackage(packageId);
        PricingResult pricing = calculatePricing(gymPackage, days, null);
        
        Map<String, Object> res = new HashMap<>();
        res.put("durationDays", days);
        res.put("grossAmount", pricing.grossAmount);
        res.put("discountPercent", pricing.discountPercent);
        res.put("finalAmount", pricing.finalAmount);
        return res;
    }

    // ----------------------------------------------------------------
    // GET APIs
    // ----------------------------------------------------------------
    public MembershipResponse getMyCurrentMembership(String email) {
        User user = getUserByEmail(email);
        Membership membership = membershipRepository.findByUser_IdAndStatus(user.getId(), "ACTIVE")
                .orElseGet(() -> membershipRepository.findByUser_IdAndStatus(user.getId(), "PAUSED").orElse(null));

        if (membership == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa đăng ký gói tập nào");
        }

        List<Transaction> transactions = transactionRepository.findByMembership_Id(membership.getId());
        Transaction latestTx = transactions.isEmpty() ? null : transactions.get(transactions.size() - 1);

        return toResponse(membership, latestTx, latestTx != null ? latestTx.getPromotion() : null, null);
    }

    public List<MembershipResponse> getMyMembershipHistory(String email) {
        User user = getUserByEmail(email);
        return membershipRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(m -> {
                    List<Transaction> txList = transactionRepository.findByMembership_Id(m.getId());
                    Transaction tx = txList.isEmpty() ? null : txList.get(txList.size() - 1);
                    return toResponse(m, tx, tx != null ? tx.getPromotion() : null, null);
                })
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private Membership getActiveOrPausedMembership(Integer userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, "ACTIVE")
                .orElseGet(() -> membershipRepository.findByUser_IdAndStatus(userId, "PAUSED")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập đang hoạt động hoặc bảo lưu.")));
    }

    private GymPackage getActivePackage(Integer packageId) {
        GymPackage pkg = gymPackageRepository.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
        if (Boolean.FALSE.equals(pkg.getIsActive())) {
            throw new IllegalArgumentException("Gói tập này hiện đã ngừng cung cấp.");
        }
        return pkg;
    }

    private User validatePtSelection(GymPackage gymPackage, Integer ptId) {
        if (Boolean.TRUE.equals(gymPackage.getCanChoosePt()) && ptId != null) {
            User pt = userRepository.findById(ptId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy PT"));
            if (!"PT".equals(pt.getRole().getName())) {
                throw new IllegalArgumentException("Người dùng được chọn không phải là PT!");
            }
            return pt;
        }
        return null;
    }

    private static class PricingResult {
        BigDecimal grossAmount;
        Integer discountPercent;
        BigDecimal finalAmount;
        Promotion promotion;
    }

    private PricingResult calculatePricing(GymPackage pkg, int days, String promoCode) {
        PricingResult res = new PricingResult();
        res.grossAmount = pkg.getDailyPrice().multiply(BigDecimal.valueOf(days));
        
        // 1. Áp chiết khấu dài hạn
        res.discountPercent = discountRepository.findBestDiscount(pkg.getId(), days).orElse(0);
        BigDecimal afterDiscount = res.grossAmount;
        if (res.discountPercent > 0) {
            afterDiscount = res.grossAmount.subtract(
                    res.grossAmount.multiply(BigDecimal.valueOf(res.discountPercent)).divide(BigDecimal.valueOf(100))
            );
        }

        res.finalAmount = afterDiscount;

        // 2. Áp mã KM
        if (promoCode != null && !promoCode.isBlank()) {
            res.promotion = promotionRepository.findValidPromotion(promoCode, LocalDate.now(), pkg.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Mã khuyến mãi không hợp lệ hoặc đã hết hạn!"));
            
            res.finalAmount = afterDiscount.subtract(
                    afterDiscount.multiply(BigDecimal.valueOf(res.promotion.getDiscountPercent())).divide(BigDecimal.valueOf(100))
            );

            res.promotion.setCurrentUsage(res.promotion.getCurrentUsage() + 1);
            promotionRepository.save(res.promotion);
        }

        return res;
    }

    private MembershipResponse toResponse(Membership m, Transaction tx, Promotion promo, Integer packageDiscountPercent) {
        return MembershipResponse.builder()
                .id(m.getId())
                .packageName(m.getGymPackage().getName())
                .dailyPrice(m.getDailyPrice() != null ? m.getDailyPrice() : m.getGymPackage().getDailyPrice())
                .durationDays(m.getDurationDays() != null ? m.getDurationDays() : (int) ChronoUnit.DAYS.between(m.getStartDate(), m.getEndDate()))
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .ptName(m.getPt() != null ? m.getPt().getFullName() : null)
                .createdAt(m.getCreatedAt())
                .holdCount(m.getHoldCount())
                .pausedAt(m.getPausedAt())
                .totalHoldDays(m.getTotalHoldDays())
                .transactionId(tx != null ? tx.getId() : null)
                .originalAmount(tx != null ? tx.getOriginalAmount() : null)
                .finalAmount(tx != null ? tx.getAmount() : null)
                .paymentMethod(tx != null ? tx.getPaymentMethod() : null)
                .transactionStatus(tx != null ? tx.getStatus() : null)
                .transactionType(tx != null ? tx.getType() : null)
                .promotionCode(promo != null ? promo.getCode() : null)
                .discountPercent(packageDiscountPercent)
                .build();
    }
}
