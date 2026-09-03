package datn_gym.service;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.request.UpgradeRequest;
import datn_gym.config.MomoProperties;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.entity.GymPackage;
import datn_gym.entity.Membership;
import datn_gym.entity.Transaction;
import datn_gym.entity.User;
import datn_gym.entity.PolicyVersion;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PackageDiscountRepository;
import datn_gym.repository.PromotionRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import datn_gym.repository.PackageHoldPolicyRepository;
import datn_gym.repository.MembershipTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceConcurrencyTest {

    @Mock MembershipRepository membershipRepository;
    @Mock GymPackageRepository gymPackageRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock PromotionRepository promotionRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock PackageDiscountRepository discountRepository;
    @Mock PtProfileRepository ptProfileRepository;
    @Mock PackageHoldPolicyRepository holdPolicyRepository;
    @Mock MembershipTransferRepository transferRepository;
    @Mock PolicyService policyService;
    @Mock SaleService saleService;

    private MembershipService service;

    @BeforeEach
    void setUp() {
        service = new MembershipService(
                membershipRepository,
                gymPackageRepository,
                userRepository,
                userService,
                promotionRepository,
                transactionRepository,
                discountRepository,
                ptProfileRepository,
                holdPolicyRepository,
                transferRepository,
                policyService,
                saleService,
                new MomoProperties(false, "https://test-payment.momo.vn", "", "", "", "", "", "GymPro", 15));
    }

    @Test
    void registerLocksMemberBeforeCheckingForCurrentMembership() {
        String email = "member@gym.local";
        User member = User.builder().id(10).email(email).fullName("Member").build();
        Membership current = Membership.builder().id(20).user(member).status("PENDING").build();
        MembershipRequest request = new MembershipRequest();
        request.setPackageId(1);
        request.setDurationDays(30);
        request.setAcceptedTerms(true);
        request.setTermsVersionId(1);
        when(policyService.requireAcceptedVersion(1, "MEMBERSHIP_TERMS"))
                .thenReturn(PolicyVersion.builder().id(1).versionNumber(1).policyType("MEMBERSHIP_TERMS").build());

        when(userRepository.findByEmailForMembershipUpdate(email))
                .thenReturn(Optional.of(member));
        when(membershipRepository.findByUser_IdAndStatusIn(
                member.getId(), List.of("ACTIVE", "PAUSED", "PENDING")))
                .thenReturn(Optional.of(current));

        assertThatThrownBy(() -> service.registerPackage(email, request, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("yêu cầu thanh toán chưa xử lý");

        verify(userRepository).findByEmailForMembershipUpdate(email);
        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    void registerUsesPersistedMembershipWhenCreatingTransaction() {
        String email = "member@gym.local";
        User member = User.builder().id(10).email(email).fullName("Member").build();
        GymPackage gymPackage = GymPackage.builder()
                .id(1)
                .name("Premium")
                .dailyPrice(BigDecimal.valueOf(83_000))
                .minDays(30)
                .canChoosePt(false)
                .isActive(true)
                .build();
        MembershipRequest request = new MembershipRequest();
        request.setPackageId(gymPackage.getId());
        request.setDurationDays(30);
        request.setPaymentMethod("BANK");
        request.setAcceptedTerms(true);
        request.setTermsVersionId(1);

        when(userRepository.findByEmailForMembershipUpdate(email)).thenReturn(Optional.of(member));
        when(policyService.requireAcceptedVersion(1, "MEMBERSHIP_TERMS"))
                .thenReturn(PolicyVersion.builder().id(1).versionNumber(1).policyType("MEMBERSHIP_TERMS").build());
        when(membershipRepository.findByUser_IdAndStatusIn(
                member.getId(), List.of("ACTIVE", "PAUSED", "PENDING")))
                .thenReturn(Optional.empty());
        when(gymPackageRepository.findById(gymPackage.getId())).thenReturn(Optional.of(gymPackage));
        when(holdPolicyRepository.findApplicable(gymPackage.getId(), 30)).thenReturn(Optional.empty());
        when(discountRepository.findBestDiscount(gymPackage.getId(), 30)).thenReturn(Optional.empty());
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> {
            Membership persisted = invocation.getArgument(0);
            assertThat(persisted.getVersion()).isNull();
            persisted.setId(101);
            persisted.setVersion(0L);
            return persisted;
        });
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction persisted = invocation.getArgument(0);
            assertThat(persisted.getMembership().getId()).isEqualTo(101);
            if (persisted.getId() == null) {
                assertThat(persisted.getVersion()).isNull();
                persisted.setId(202);
                persisted.setVersion(0L);
            }
            return persisted;
        });

        MembershipResponse response = service.registerPackage(email, request, null, null);

        assertThat(response.getId()).isEqualTo(101);
        assertThat(response.getTransactionId()).isEqualTo(202);
        assertThat(response.getStatus()).isEqualTo("PENDING");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void upgradeFromPackageWithPtKeepsCurrentPtAndIgnoresNewSelection() {
        String email = "member@gym.local";
        User member = User.builder().id(10).email(email).fullName("Member").build();
        User currentPt = User.builder().id(20).fullName("PT hiện tại").build();
        GymPackage premium = GymPackage.builder()
                .id(2).name("PREMIUM").dailyPrice(BigDecimal.valueOf(50_000))
                .hasPt(true).canChoosePt(true).minDays(30).isActive(true).build();
        GymPackage vip = GymPackage.builder()
                .id(3).name("VIP").dailyPrice(BigDecimal.valueOf(83_000))
                .hasPt(true).canChoosePt(true).minDays(30).isActive(true).build();
        Membership membership = Membership.builder()
                .id(30).user(member).gymPackage(premium).pt(currentPt)
                .dailyPrice(premium.getDailyPrice()).durationDays(60)
                .startDate(LocalDate.now().minusDays(30)).endDate(LocalDate.now().plusDays(30))
                .status("ACTIVE").build();
        UpgradeRequest request = new UpgradeRequest();
        request.setNewPackageId(vip.getId());
        request.setExtraDays(0);
        request.setPaymentMethod("CASH");
        request.setPtId(999);

        when(userRepository.findByEmailForMembershipUpdate(email)).thenReturn(Optional.of(member));
        when(membershipRepository.findByUser_IdAndStatusAndEndDateGreaterThanEqual(
                member.getId(), "ACTIVE", LocalDate.now())).thenReturn(Optional.of(membership));
        when(gymPackageRepository.findById(vip.getId())).thenReturn(Optional.of(vip));
        when(discountRepository.findBestDiscount(vip.getId(), 30)).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(301);
            assertThat(transaction.getRequestedPt()).isSameAs(currentPt);
            return transaction;
        });

        MembershipResponse response = service.upgradeMembership(email, request);

        assertThat(response.getTransactionId()).isEqualTo(301);
        verify(userRepository, never()).findById(999);
        verify(ptProfileRepository, never()).findByUser_Id(999);
    }
}
