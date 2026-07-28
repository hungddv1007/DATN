package datn_gym.service;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.request.RenewRequest;
import datn_gym.dto.request.UpgradeRequest;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.dto.response.PricePreviewResponse;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GymPackageRepository gymPackageRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final TransactionRepository transactionRepository;
    private final PackageDiscountRepository discountRepository;
    private final PtProfileRepository ptProfileRepository;

    // ================================================================
    // A. ĐĂNG KÝ GÓI MỚI (NEW)
    // ================================================================
    @Transactional
    public MembershipResponse registerPackage(String memberEmail, MembershipRequest request) {
        User member = userService.getUserByEmail(memberEmail);

        // Kiểm tra đã có gói ACTIVE chưa
        membershipRepository.findByUser_IdAndStatus(member.getId(), "ACTIVE")
                .ifPresent(m -> {
                    throw new IllegalArgumentException(
                            "Bạn đang có gói tập đang hoạt động. Vui lòng gia hạn, nâng cấp hoặc hủy gói hiện tại!");
                });

        GymPackage gymPackage = getActivePackage(request.getPackageId());

        // Kiểm tra min_days
        if (request.getDurationDays() < gymPackage.getMinDays()) {
            throw new IllegalArgumentException(
                    "Gói " + gymPackage.getName() + " yêu cầu đăng ký tối thiểu " + gymPackage.getMinDays() + " ngày!");
        }

        // Xử lý PT
        User pt = resolvePt(gymPackage, request.getPtId());

        // Tính tiền
        PriceCalc calc = calculatePrice(gymPackage, request.getDurationDays(),
                request.getPromotionCode(), BigDecimal.ZERO);

        // Tạo Membership
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getDurationDays());

        Membership membership = Membership.builder()
                .user(member)
                .gymPackage(gymPackage)
                .pt(pt)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .durationDays(request.getDurationDays())
                .dailyPrice(gymPackage.getDailyPrice())
                .build();

        membershipRepository.save(membership);

        // Tạo Transaction
        Transaction transaction = createTransaction(membership, calc, request.getPaymentMethod(), "NEW");

        return toResponse(membership, transaction);
    }

    // ================================================================
    // B. GIA HẠN (RENEW) — cùng gói
    // ================================================================
    @Transactional
    public MembershipResponse renewMembership(String memberEmail, RenewRequest request) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = getActiveMembership(member.getId());
        GymPackage gymPackage = membership.getGymPackage();

        if (request.getDurationDays() < gymPackage.getMinDays()) {
            throw new IllegalArgumentException(
                    "Gói " + gymPackage.getName() + " yêu cầu gia hạn tối thiểu " + gymPackage.getMinDays() + " ngày!");
        }

        // Tính tiền (dùng daily_price hiện tại của gói)
        PriceCalc calc = calculatePrice(gymPackage, request.getDurationDays(),
                request.getPromotionCode(), BigDecimal.ZERO);

        // Cập nhật endDate
        membership.setEndDate(membership.getEndDate().plusDays(request.getDurationDays()));
        membership.setDurationDays(membership.getDurationDays() + request.getDurationDays());
        membershipRepository.save(membership);

        Transaction transaction = createTransaction(membership, calc, request.getPaymentMethod(), "RENEW");

        return toResponse(membership, transaction);
    }

    // ================================================================
    // C. NÂNG CẤP (UPGRADE) — có hoặc không gia hạn thêm
    // ================================================================
    @Transactional
    public MembershipResponse upgradeMembership(String memberEmail, UpgradeRequest request) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = getActiveMembership(member.getId());
        GymPackage oldPackage = membership.getGymPackage();
        GymPackage newPackage = getActivePackage(request.getNewPackageId());

        if (newPackage.getDailyPrice().compareTo(oldPackage.getDailyPrice()) <= 0) {
            throw new IllegalArgumentException("Chỉ có thể nâng cấp lên gói cao hơn!");
        }

        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
        if (remainingDays <= 0) {
            throw new IllegalArgumentException("Gói tập đã hết hạn! Vui lòng đăng ký gói mới.");
        }

        int extraDays = (request.getExtraDays() != null && request.getExtraDays() > 0)
                ? request.getExtraDays() : 0;

        // Nếu nâng cấp tại chỗ, check remaining >= minDays gói mới
        if (extraDays == 0 && remainingDays < newPackage.getMinDays()) {
            throw new IllegalArgumentException(
                    "Gói " + newPackage.getName() + " yêu cầu tối thiểu " + newPackage.getMinDays()
                            + " ngày. Bạn chỉ còn " + remainingDays + " ngày. Hãy gia hạn thêm!");
        }

        int totalNewDays = (int) remainingDays + extraDays;

        // PRORATION: credit từ gói cũ
        BigDecimal credit = membership.getDailyPrice()
                .multiply(BigDecimal.valueOf(remainingDays));

        // Tính giá gói mới
        PriceCalc calc = calculatePrice(newPackage, totalNewDays,
                request.getPromotionCode(), credit);

        // Xử lý PT mới (nếu gói mới có canChoosePt)
        User pt = resolvePt(newPackage, request.getPtId());

        // Cập nhật membership
        membership.setGymPackage(newPackage);
        membership.setDailyPrice(newPackage.getDailyPrice());
        if (pt != null) membership.setPt(pt);
        if (extraDays > 0) {
            membership.setEndDate(membership.getEndDate().plusDays(extraDays));
            membership.setDurationDays(membership.getDurationDays() + extraDays);
        }
        membershipRepository.save(membership);

        Transaction transaction = createTransaction(membership, calc,
                request.getPaymentMethod(), "UPGRADE");

        return toResponse(membership, transaction);
    }

    // ================================================================
    // D. BẢO LƯU (PAUSE)
    // ================================================================
    @Transactional
    public MembershipResponse pauseMembership(String memberEmail) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = getActiveMembership(member.getId());
        GymPackage gymPackage = membership.getGymPackage();

        if (gymPackage.getMaxHoldTimes() <= 0) {
            throw new IllegalArgumentException("Gói " + gymPackage.getName() + " không hỗ trợ bảo lưu!");
        }

        if (membership.getHoldCount() >= gymPackage.getMaxHoldTimes()) {
            throw new IllegalArgumentException(
                    "Bạn đã sử dụng hết " + gymPackage.getMaxHoldTimes() + " lượt bảo lưu cho gói này!");
        }

        membership.setStatus("PAUSED");
        membership.setPausedAt(LocalDate.now());
        membership.setHoldCount(membership.getHoldCount() + 1);
        membershipRepository.save(membership);

        return toResponse(membership, getLatestTransaction(membership.getId()));
    }

    // ================================================================
    // E. HỦY BẢO LƯU (RESUME)
    // ================================================================
    @Transactional
    public MembershipResponse resumeMembership(String memberEmail) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = membershipRepository.findByUser_IdAndStatus(member.getId(), "PAUSED")
                .orElseThrow(() -> new IllegalArgumentException("Bạn không có gói đang bảo lưu!"));

        GymPackage gymPackage = membership.getGymPackage();
        LocalDate pausedAt = membership.getPausedAt();
        long holdDays = ChronoUnit.DAYS.between(pausedAt, LocalDate.now());

        // Tính return_days = holdDays × holdReturnPercent / 100
        int returnPercent = gymPackage.getHoldReturnPercent() != null ? gymPackage.getHoldReturnPercent() : 0;
        long returnDays = (long) Math.floor(holdDays * returnPercent / 100.0);

        membership.setEndDate(membership.getEndDate().plusDays(returnDays));
        membership.setStatus("ACTIVE");
        membership.setPausedAt(null);
        membership.setTotalHoldDays(membership.getTotalHoldDays() + (int) holdDays);
        membershipRepository.save(membership);

        return toResponse(membership, getLatestTransaction(membership.getId()));
    }

    // ================================================================
    // F. HỦY GÓI (CANCEL)
    // ================================================================
    @Transactional
    public MembershipResponse cancelMembership(String memberEmail) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = membershipRepository.findByUser_IdAndStatusIn(
                member.getId(), List.of("ACTIVE", "PAUSED"))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập để hủy!"));

        membership.setStatus("CANCELLED");
        membership.setPausedAt(null);
        membershipRepository.save(membership);

        return toResponse(membership, getLatestTransaction(membership.getId()));
    }

    // ================================================================
    // PREVIEW ENDPOINTS (xem trước giá)
    // ================================================================
    public PricePreviewResponse previewRenew(String memberEmail, int days) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = getActiveMembership(member.getId());
        GymPackage pkg = membership.getGymPackage();

        int discountPct = discountRepository.findBestDiscount(pkg.getId(), days).orElse(0);
        BigDecimal gross = pkg.getDailyPrice().multiply(BigDecimal.valueOf(days));
        BigDecimal afterDiscount = applyPercent(gross, discountPct);

        return PricePreviewResponse.builder()
                .currentPackageName(pkg.getName())
                .newPackageName(pkg.getName())
                .currentDailyPrice(pkg.getDailyPrice())
                .newDailyPrice(pkg.getDailyPrice())
                .remainingDays((int) ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate()))
                .extraDays(days)
                .totalNewDays(days)
                .grossAmount(gross)
                .longTermDiscount(discountPct)
                .afterDiscount(afterDiscount)
                .credit(BigDecimal.ZERO)
                .finalAmount(afterDiscount)
                .type("RENEW")
                .build();
    }

    public PricePreviewResponse previewUpgrade(String memberEmail, int newPackageId, Integer extraDays) {
        User member = userService.getUserByEmail(memberEmail);
        Membership membership = getActiveMembership(member.getId());
        GymPackage oldPkg = membership.getGymPackage();
        GymPackage newPkg = getActivePackage(newPackageId);

        long remaining = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
        int extra = (extraDays != null && extraDays > 0) ? extraDays : 0;
        int totalNewDays = (int) remaining + extra;

        BigDecimal credit = membership.getDailyPrice().multiply(BigDecimal.valueOf(remaining));
        int discountPct = discountRepository.findBestDiscount(newPkg.getId(), totalNewDays).orElse(0);
        BigDecimal gross = newPkg.getDailyPrice().multiply(BigDecimal.valueOf(totalNewDays));
        BigDecimal afterDiscount = applyPercent(gross, discountPct);
        BigDecimal finalAmt = afterDiscount.subtract(credit).max(BigDecimal.ZERO);

        return PricePreviewResponse.builder()
                .currentPackageName(oldPkg.getName())
                .newPackageName(newPkg.getName())
                .currentDailyPrice(membership.getDailyPrice())
                .newDailyPrice(newPkg.getDailyPrice())
                .remainingDays((int) remaining)
                .extraDays(extra)
                .totalNewDays(totalNewDays)
                .grossAmount(gross)
                .longTermDiscount(discountPct)
                .afterDiscount(afterDiscount)
                .credit(credit)
                .finalAmount(finalAmt)
                .type(extra > 0 ? "UPGRADE_RENEW" : "UPGRADE")
                .build();
    }

    // ================================================================
    // XEM GÓI HIỆN TẠI + LỊCH SỬ
    // ================================================================
    public MembershipResponse getMyCurrentMembership(String email) {
        User user = userService.getUserByEmail(email);
        Membership membership = membershipRepository.findByUser_IdAndStatusIn(
                user.getId(), List.of("ACTIVE", "PAUSED"))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bạn chưa đăng ký gói tập nào"));
        return toResponse(membership, getLatestTransaction(membership.getId()));
    }

    public List<MembershipResponse> getMyMembershipHistory(String email) {
        User user = userService.getUserByEmail(email);
        return membershipRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(m -> toResponse(m, getLatestTransaction(m.getId())))
                .collect(Collectors.toList());
    }

    // ================================================================
    // HELPERS
    // ================================================================


    private GymPackage getActivePackage(Integer packageId) {
        GymPackage pkg = gymPackageRepository.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
        if (Boolean.FALSE.equals(pkg.getIsActive())) {
            throw new IllegalArgumentException("Gói tập này hiện đã ngừng cung cấp.");
        }
        return pkg;
    }

    private Membership getActiveMembership(Integer userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bạn không có gói tập đang hoạt động!"));
    }

    private User resolvePt(GymPackage gymPackage, Integer ptId) {
        if (Boolean.TRUE.equals(gymPackage.getCanChoosePt()) && ptId != null) {
            User pt = userRepository.findById(ptId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy PT"));
            if (!"PT".equals(pt.getRole().getName())) {
                throw new IllegalArgumentException("Người dùng được chọn không phải là PT!");
            }

            // Kiểm tra số lượng học viên tối đa của PT
            PtProfile ptProfile = ptProfileRepository.findByUser_Id(pt.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ của Huấn luyện viên!"));
            long activeMembers = membershipRepository.countByPt_IdAndStatus(pt.getId(), "ACTIVE");
            int maxMembers = ptProfile.getMaxMembers() != null ? ptProfile.getMaxMembers() : 5;
            if (activeMembers >= maxMembers) {
                throw new IllegalArgumentException("Huấn luyện viên " + pt.getFullName() + " đã nhận đủ số lượng học viên tối đa (" + maxMembers + ")!");
            }

            return pt;
        }
        return null;
    }

    private Transaction getLatestTransaction(Integer membershipId) {
        List<Transaction> txList = transactionRepository.findByMembership_Id(membershipId);
        return txList.isEmpty() ? null : txList.get(0);
    }

    // --- Tính giá ---

    private static class PriceCalc {
        BigDecimal grossAmount;
        BigDecimal afterDiscount;
        BigDecimal finalAmount;
        int longTermDiscountPercent;
        Promotion promotion;
    }

    private PriceCalc calculatePrice(GymPackage pkg, int days, String promoCode, BigDecimal credit) {
        PriceCalc calc = new PriceCalc();

        calc.grossAmount = pkg.getDailyPrice().multiply(BigDecimal.valueOf(days));

        // Chiết khấu dài hạn
        calc.longTermDiscountPercent = discountRepository.findBestDiscount(pkg.getId(), days).orElse(0);
        calc.afterDiscount = applyPercent(calc.grossAmount, calc.longTermDiscountPercent);

        // Mã khuyến mãi
        calc.promotion = null;
        BigDecimal afterPromo = calc.afterDiscount;

        if (promoCode != null && !promoCode.isBlank()) {
            Promotion promotion = promotionRepository.findValidPromotion(
                    promoCode, LocalDate.now(), pkg.getId()
            ).orElseThrow(() -> new IllegalArgumentException("Mã khuyến mãi không hợp lệ hoặc đã hết hạn!"));

            afterPromo = applyPercent(calc.afterDiscount, promotion.getDiscountPercent());
            promotion.setCurrentUsage(promotion.getCurrentUsage() + 1);
            promotionRepository.save(promotion);
            calc.promotion = promotion;
        }

        // Trừ credit (proration)
        calc.finalAmount = afterPromo.subtract(credit).max(BigDecimal.ZERO);
        return calc;
    }

    private BigDecimal applyPercent(BigDecimal amount, int discountPercent) {
        if (discountPercent <= 0) return amount;
        return amount.multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    private Transaction createTransaction(Membership membership, PriceCalc calc,
                                           String paymentMethod, String type) {
        Transaction transaction = Transaction.builder()
                .membership(membership)
                .promotion(calc.promotion)
                .originalAmount(calc.grossAmount)
                .amount(calc.finalAmount)
                .paymentMethod(paymentMethod != null ? paymentMethod : "BANK")
                .type(type)
                .status("PENDING")
                .build();
        transactionRepository.save(transaction);
        return transaction;
    }

    private MembershipResponse toResponse(Membership m, Transaction tx) {
        long remainingDays = 0;
        if ("ACTIVE".equals(m.getStatus())) {
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), m.getEndDate());
            if (remainingDays < 0) remainingDays = 0;
        }

        GymPackage pkg = m.getGymPackage();

        return MembershipResponse.builder()
                .id(m.getId())
                .packageId(pkg.getId())
                .packageName(pkg.getName())
                .dailyPrice(m.getDailyPrice())
                .durationDays(m.getDurationDays())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .ptName(m.getPt() != null ? m.getPt().getFullName() : null)
                .ptId(m.getPt() != null ? m.getPt().getId() : null)
                .createdAt(m.getCreatedAt())
                .holdCount(m.getHoldCount())
                .maxHoldTimes(pkg.getMaxHoldTimes())
                .holdReturnPercent(pkg.getHoldReturnPercent())
                .totalHoldDays(m.getTotalHoldDays())
                .pausedAt(m.getPausedAt())
                .transactionId(tx != null ? tx.getId() : null)
                .transactionType(tx != null ? tx.getType() : null)
                .originalAmount(tx != null ? tx.getOriginalAmount() : null)
                .finalAmount(tx != null ? tx.getAmount() : null)
                .paymentMethod(tx != null ? tx.getPaymentMethod() : null)
                .transactionStatus(tx != null ? tx.getStatus() : null)
                .promotionCode(tx != null && tx.getPromotion() != null ? tx.getPromotion().getCode() : null)
                .discountPercent(tx != null && tx.getPromotion() != null ? tx.getPromotion().getDiscountPercent() : null)
                .remainingDays(remainingDays)
                .build();
    }
}
