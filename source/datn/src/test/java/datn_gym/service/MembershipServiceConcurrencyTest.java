package datn_gym.service;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.entity.Membership;
import datn_gym.entity.User;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PackageDiscountRepository;
import datn_gym.repository.PromotionRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
                ptProfileRepository);
    }

    @Test
    void registerLocksMemberBeforeCheckingForCurrentMembership() {
        String email = "member@gym.local";
        User member = User.builder().id(10).email(email).fullName("Member").build();
        Membership current = Membership.builder().id(20).user(member).status("PENDING").build();
        MembershipRequest request = new MembershipRequest();
        request.setPackageId(1);
        request.setDurationDays(30);

        when(userRepository.findByEmailForMembershipUpdate(email))
                .thenReturn(Optional.of(member));
        when(membershipRepository.findByUser_IdAndStatusIn(
                member.getId(), List.of("ACTIVE", "PAUSED", "PENDING")))
                .thenReturn(Optional.of(current));

        assertThatThrownBy(() -> service.registerPackage(email, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("yêu cầu thanh toán chưa xử lý");

        verify(userRepository).findByEmailForMembershipUpdate(email);
        verify(membershipRepository, never()).save(any(Membership.class));
    }
}
