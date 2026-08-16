package datn_gym.service;

import datn_gym.config.PaymentProperties;
import datn_gym.entity.GymPackage;
import datn_gym.entity.Membership;
import datn_gym.entity.Promotion;
import datn_gym.entity.Transaction;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PromotionRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import datn_gym.repository.PackageHoldPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock private SaleService saleService;
    @Mock private EmailService emailService;
    @Mock private PackageHoldPolicyRepository holdPolicyRepository;

    private TransactionService service;
    private User admin;

    @BeforeEach
    void setUp() {
        service = new TransactionService(
                transactionRepository,
                membershipRepository,
                promotionRepository,
                userRepository,
                new PaymentProperties("", "", "", "GYMPRO", 24),
                saleService, emailService, holdPolicyRepository);
        admin = User.builder().id(99).email("admin@gym.local").fullName("Admin").build();
        org.mockito.Mockito.lenient().when(holdPolicyRepository.findApplicable(
                org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(Optional.empty());
    }

    @Test
    void confirmRenewAppliesDaysOnlyAfterApproval() {
        givenAdmin();
        Membership membership = activeMembership();
        Transaction transaction = pendingTransaction("RENEW", membership);
        transaction.setRequestedDurationDays(30);
        transaction.setRequestedPackage(membership.getGymPackage());
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        service.confirmTransaction(1, admin.getEmail());

        assertThat(membership.getEndDate()).isEqualTo(LocalDate.now().plusDays(40));
        assertThat(membership.getDurationDays()).isEqualTo(60);
        assertThat(transaction.getStatus()).isEqualTo("CONFIRMED");
        assertThat(transaction.getOperationApplied()).isTrue();
        assertThat(transaction.getConfirmedBy()).isSameAs(admin);
        verify(membershipRepository).save(membership);
    }

    @Test
    void cancelUpgradeKeepsActiveMembershipUnchanged() {
        givenAdmin();
        Membership membership = activeMembership();
        GymPackage originalPackage = membership.getGymPackage();
        Transaction transaction = pendingTransaction("UPGRADE", membership);
        transaction.setRequestedDurationDays(15);
        transaction.setRequestedPackage(packageWithId(2, "VIP", 50_000));
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        service.cancelTransaction(1, admin.getEmail());

        assertThat(membership.getStatus()).isEqualTo("ACTIVE");
        assertThat(membership.getGymPackage()).isSameAs(originalPackage);
        assertThat(membership.getEndDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(transaction.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelNewCancelsPendingMembershipAndReleasesPromotion() {
        givenAdmin();
        Membership membership = activeMembership();
        membership.setStatus("PENDING");
        Promotion promotion = Promotion.builder().id(7).currentUsage(2).build();
        Transaction transaction = pendingTransaction("NEW", membership);
        transaction.setPromotion(promotion);
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
        when(promotionRepository.findByIdForUpdate(7)).thenReturn(Optional.of(promotion));

        service.cancelTransaction(1, admin.getEmail());

        assertThat(membership.getStatus()).isEqualTo("CANCELLED");
        assertThat(promotion.getCurrentUsage()).isEqualTo(1);
        verify(membershipRepository).save(membership);
        verify(promotionRepository).findByIdForUpdate(7);
        verify(promotionRepository).save(promotion);
    }

    @Test
    void expiresOldPendingTransactionAndReleasesReservedPromotion() {
        Membership membership = activeMembership();
        membership.setStatus("PENDING");
        Promotion promotion = Promotion.builder().id(7).currentUsage(1).build();
        Transaction transaction = pendingTransaction("NEW", membership);
        transaction.setPromotion(promotion);
        transaction.setCreatedAt(LocalDateTime.now().minusHours(25));
        when(promotionRepository.findByIdForUpdate(7)).thenReturn(Optional.of(promotion));
        when(transactionRepository.findByStatusAndCreatedAtBefore(
                org.mockito.ArgumentMatchers.eq("PENDING"),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(transaction));

        int cancelled = service.expirePendingTransactions();

        assertThat(cancelled).isEqualTo(1);
        assertThat(transaction.getStatus()).isEqualTo("CANCELLED");
        assertThat(membership.getStatus()).isEqualTo("CANCELLED");
        assertThat(promotion.getCurrentUsage()).isZero();
    }

    private void givenAdmin() {
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
    }

    private Membership activeMembership() {
        return Membership.builder()
                .id(10)
                .user(User.builder()
                        .id(5)
                        .email("member@gym.local")
                        .fullName("Member")
                        .build())
                .gymPackage(packageWithId(1, "Basic", 20_000))
                .startDate(LocalDate.now().minusDays(20))
                .endDate(LocalDate.now().plusDays(10))
                .durationDays(30)
                .dailyPrice(BigDecimal.valueOf(20_000))
                .status("ACTIVE")
                .build();
    }

    private Transaction pendingTransaction(String type, Membership membership) {
        return Transaction.builder()
                .id(1)
                .membership(membership)
                .amount(BigDecimal.valueOf(100_000))
                .originalAmount(BigDecimal.valueOf(100_000))
                .paymentMethod("BANK")
                .type(type)
                .status("PENDING")
                .build();
    }

    private GymPackage packageWithId(int id, String name, long dailyPrice) {
        return GymPackage.builder()
                .id(id)
                .name(name)
                .dailyPrice(BigDecimal.valueOf(dailyPrice))
                .build();
    }
}
