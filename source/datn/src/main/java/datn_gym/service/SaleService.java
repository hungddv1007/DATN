package datn_gym.service;

import datn_gym.dto.request.CreateSaleAccountRequest;
import datn_gym.dto.request.SaleCodeRequest;
import datn_gym.dto.response.*;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private static final List<String> ACTIVE_CHAT_STATUSES = List.of("SALE_ASSIGNED", "SALE_JOINED");

    private final SaleProfileRepository profileRepository;
    private final SaleReferralCodeRepository codeRepository;
    private final SalesCodeRedemptionRepository redemptionRepository;
    private final CommissionRecordRepository commissionRepository;
    private final AiConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileResponse createSaleAccount(CreateSaleAccountRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPhone = request.getPhone() == null || request.getPhone().isBlank()
                ? null : request.getPhone().trim();
        if (userRepository.existsByEmail(normalizedEmail)) throw new IllegalArgumentException("Email đã tồn tại");
        if (normalizedPhone != null && userRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        Role saleRole = roleRepository.findByName("SALE")
                .orElseThrow(() -> new IllegalStateException("Chưa cấu hình role SALE"));
        User user = userRepository.save(User.builder().role(saleRole).email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword())).fullName(request.getFullName().trim())
                .phone(normalizedPhone).provider("LOCAL").status(true).build());
        profileRepository.save(SaleProfile.builder().user(user).build());
        return UserProfileResponse.builder().id(user.getId()).email(user.getEmail()).fullName(user.getFullName())
                .phone(user.getPhone()).avatar(user.getAvatar()).role("SALE").status(true).createdAt(user.getCreatedAt()).build();
    }

    public SaleProfile requireProfile(String email) {
        return profileRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ SALE"));
    }

    public List<UserProfileResponse> getSaleAccounts() {
        return profileRepository.findAll().stream().map(profile -> {
            User user = profile.getUser();
            return UserProfileResponse.builder().id(user.getId()).email(user.getEmail())
                    .fullName(user.getFullName()).phone(user.getPhone()).avatar(user.getAvatar())
                    .role("SALE").status(user.getStatus()).createdAt(user.getCreatedAt()).build();
        }).toList();
    }

    public SaleReferralCode resolveUsableCode(String rawCode, User member) {
        if (rawCode == null || rawCode.isBlank()) return null;
        SaleReferralCode code = codeRepository.findByCodeForUpdate(rawCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Mã giới thiệu không hợp lệ"));
        if (!codeRepository.isUsable(code)) throw new IllegalArgumentException("Mã giới thiệu đã hết hạn hoặc bị khóa");
        if (Boolean.TRUE.equals(code.getOneTimePerMember())
                && redemptionRepository.existsBySaleCode_IdAndMember_IdAndStatusIn(
                code.getId(), member.getId(), List.of("RESERVED", "CONFIRMED"))) {
            throw new IllegalArgumentException("Bạn đã sử dụng mã giới thiệu này trước đây");
        }
        return code;
    }

    @Transactional
    public void reserveRedemption(SaleReferralCode code, User member, Transaction transaction) {
        if (code == null) return;
        redemptionRepository.save(SalesCodeRedemption.builder().saleCode(code).member(member)
                .transaction(transaction).status("RESERVED").build());
    }

    @Transactional
    public void confirmRedemptionAndCommission(Transaction transaction) {
        if (transaction.getSaleCode() == null) return;
        SalesCodeRedemption redemption = redemptionRepository.findByTransaction_Id(transaction.getId())
                .orElseThrow(() -> new IllegalStateException("Thiếu lượt sử dụng mã SALE"));
        redemption.setStatus("CONFIRMED");
        redemption.setConfirmedAt(LocalDateTime.now());
        redemptionRepository.save(redemption);

        SaleProfile profile = redemption.getSaleCode().getSalesProfile();
        int rate = profile.commissionRate();
        BigDecimal amount = transaction.getAmount().multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        commissionRepository.save(CommissionRecord.builder().transaction(transaction).salesProfile(profile)
                .baseAmount(transaction.getAmount()).commissionRate(rate).commissionAmount(amount)
                .status("PENDING").payableAt(LocalDateTime.now().plusDays(7)).build());
        refreshLevel(profile);
    }

    @Transactional
    public void releaseRedemption(Transaction transaction) {
        redemptionRepository.findByTransaction_Id(transaction.getId()).ifPresent(redemption -> {
            redemption.setStatus("RELEASED");
            redemptionRepository.save(redemption);
        });
    }

    private void refreshLevel(SaleProfile profile) {
        int customers = Math.toIntExact(redemptionRepository.countDistinctConfirmedMembers(profile.getId()));
        int newLevel = customers >= 30 ? 3 : customers >= 10 ? 2 : 1;
        profile.setSuccessfulCustomers(customers);
        if (newLevel > profile.getLevelNumber()) profile.setLevelNumber(newLevel);
        profileRepository.save(profile);
        int discount = profile.discountPercent();
        codeRepository.findBySalesProfile_IdOrderByCreatedAtDesc(profile.getId()).stream()
                .filter(code -> Boolean.TRUE.equals(code.getIsActive()))
                .forEach(code -> { code.setDiscountPercent(discount); codeRepository.save(code); });
    }

    @Transactional
    public SaleCodeResponse createCode(String email, SaleCodeRequest request) {
        SaleProfile profile = requireProfile(email);
        if (codeRepository.countBySalesProfile_IdAndIsActiveTrue(profile.getId()) >= 3) {
            throw new IllegalArgumentException("Bạn chỉ được có tối đa 3 mã đang hoạt động");
        }
        String codeValue = request.getCode().trim().toUpperCase();
        if (!codeValue.matches("[A-Z0-9_-]{4,50}")) throw new IllegalArgumentException("Mã chỉ gồm chữ, số, _ hoặc -");
        if (codeRepository.existsByCodeIgnoreCase(codeValue)) throw new IllegalArgumentException("Mã này đã tồn tại");
        SaleReferralCode code = codeRepository.save(SaleReferralCode.builder().salesProfile(profile).code(codeValue)
                .description(request.getDescription()).discountPercent(profile.discountPercent())
                .oneTimePerMember(request.isOneTimePerMember()).expiresAt(request.getExpiresAt()).build());
        return toCodeResponse(code);
    }

    public List<SaleCodeResponse> getCodes(String email) {
        SaleProfile profile = requireProfile(email);
        return codeRepository.findBySalesProfile_IdOrderByCreatedAtDesc(profile.getId()).stream()
                .map(this::toCodeResponse).toList();
    }

    @Transactional
    public SaleCodeResponse setCodeActive(String email, Integer id, boolean active) {
        SaleProfile profile = requireProfile(email);
        SaleReferralCode code = codeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã"));
        if (!code.getSalesProfile().getId().equals(profile.getId())) throw new IllegalArgumentException("Không có quyền sửa mã này");
        if (active && !Boolean.TRUE.equals(code.getIsActive())
                && codeRepository.countBySalesProfile_IdAndIsActiveTrue(profile.getId()) >= 3) {
            throw new IllegalArgumentException("Bạn chỉ được có tối đa 3 mã đang hoạt động");
        }
        code.setIsActive(active);
        return toCodeResponse(codeRepository.save(code));
    }

    @Transactional
    public SaleDashboardResponse setOnline(String email, boolean online) {
        SaleProfile profile = requireProfile(email);
        profile.setIsOnline(online);
        profileRepository.save(profile);
        return dashboard(email);
    }

    public SaleDashboardResponse dashboard(String email) {
        SaleProfile profile = requireProfile(email);
        List<CommissionRecord> commissions = commissionRepository.findBySalesProfile_IdOrderByCreatedAtDesc(profile.getId());
        BigDecimal pending = commissions.stream().filter(c -> List.of("PENDING", "PAYABLE").contains(c.getStatus()))
                .map(CommissionRecord::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = commissions.stream().filter(c -> "PAID".equals(c.getStatus()))
                .map(CommissionRecord::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return SaleDashboardResponse.builder().level(profile.getLevelNumber())
                .successfulCustomers(profile.getSuccessfulCustomers())
                .nextLevelTarget(profile.getLevelNumber() == 1 ? 10 : profile.getLevelNumber() == 2 ? 30 : null)
                .discountPercent(profile.discountPercent()).commissionRate(profile.commissionRate())
                .activeCodes(codeRepository.countBySalesProfile_IdAndIsActiveTrue(profile.getId()))
                .activeChats(conversationRepository.countByAssignedSale_IdAndHandoffStatusIn(profile.getUser().getId(), ACTIVE_CHAT_STATUSES))
                .pendingCommission(pending).paidCommission(paid).online(Boolean.TRUE.equals(profile.getIsOnline())).build();
    }

    public List<CommissionResponse> getCommissions(String email) {
        SaleProfile profile = requireProfile(email);
        return commissionRepository.findBySalesProfile_IdOrderByCreatedAtDesc(profile.getId()).stream().map(this::toCommission).toList();
    }

    public List<CommissionResponse> getAllCommissions() {
        return commissionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toCommission).toList();
    }

    public SaleProfile findBestAvailableSale() {
        return profileRepository.findByIsOnlineTrueAndUser_StatusTrueOrderByIdAsc().stream()
                .filter(p -> conversationRepository.countByAssignedSale_IdAndHandoffStatusIn(p.getUser().getId(), ACTIVE_CHAT_STATUSES)
                        < p.getMaxConcurrentChats())
                .min(Comparator.comparingLong(p -> conversationRepository.countByAssignedSale_IdAndHandoffStatusIn(
                        p.getUser().getId(), ACTIVE_CHAT_STATUSES)))
                .orElse(null);
    }

    @Scheduled(cron = "0 10 * * * *")
    @Transactional
    public void markPayableCommissions() {
        commissionRepository.findAll().stream()
                .filter(c -> "PENDING".equals(c.getStatus()) && c.getPayableAt() != null && !c.getPayableAt().isAfter(LocalDateTime.now()))
                .forEach(c -> { c.setStatus("PAYABLE"); commissionRepository.save(c); });
    }

    @Transactional
    public CommissionResponse markCommissionPaid(Long id) {
        CommissionRecord c = commissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hoa hồng"));
        if (!"PAYABLE".equals(c.getStatus())) {
            throw new IllegalArgumentException("Chỉ hoa hồng đã qua thời gian chờ mới có thể thanh toán");
        }
        c.setStatus("PAID"); c.setPaidAt(LocalDateTime.now());
        return toCommission(commissionRepository.save(c));
    }

    private SaleCodeResponse toCodeResponse(SaleReferralCode c) {
        return SaleCodeResponse.builder().id(c.getId()).code(c.getCode()).description(c.getDescription())
                .discountPercent(c.getDiscountPercent()).oneTimePerMember(Boolean.TRUE.equals(c.getOneTimePerMember()))
                .active(Boolean.TRUE.equals(c.getIsActive())).expiresAt(c.getExpiresAt()).createdAt(c.getCreatedAt()).build();
    }

    private CommissionResponse toCommission(CommissionRecord c) {
        return CommissionResponse.builder().id(c.getId()).transactionId(c.getTransaction().getId())
                .memberName(c.getTransaction().getMembership().getUser().getFullName())
                .saleName(c.getSalesProfile().getUser().getFullName())
                .saleEmail(c.getSalesProfile().getUser().getEmail()).baseAmount(c.getBaseAmount())
                .commissionRate(c.getCommissionRate()).commissionAmount(c.getCommissionAmount())
                .status(c.getStatus()).createdAt(c.getCreatedAt())
                .payableAt(c.getPayableAt()).paidAt(c.getPaidAt()).build();
    }
}
