package datn_gym.service;

import datn_gym.entity.MemberProfile;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatContextServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private DietRepository dietRepository;
    @Mock
    private PtScheduleRepository scheduleRepository;
    @Mock
    private MemberProfileRepository profileRepository;

    private AiChatContextService service;
    private User member;

    @BeforeEach
    void setUp() {
        service = new AiChatContextService(
                userService,
                membershipRepository,
                dietRepository,
                scheduleRepository,
                profileRepository);
        member = User.builder()
                .id(5)
                .email("member@gym.local")
                .fullName("Hội viên")
                .build();
        when(userService.getUserByEmail(member.getEmail())).thenReturn(member);
        when(membershipRepository.findByUser_IdOrderByCreatedAtDesc(member.getId()))
                .thenReturn(List.of());
        when(dietRepository.findByMember_IdOrderByCreatedAtDesc(member.getId()))
                .thenReturn(List.of());
        when(scheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
                        eq(member.getId()),
                        any(LocalDate.class),
                        any(LocalDate.class),
                        eq("ACTIVE")))
                .thenReturn(List.of());
    }

    @Test
    void doesNotReadPhysicalProfileWithoutConsent() {
        String context = service.buildMemberContext(member.getEmail(), false);

        assertThat(context).contains("không được chia sẻ");
        verify(profileRepository, never()).findByUser_Id(member.getId());
    }

    @Test
    void includesPhysicalProfileAfterConsent() {
        when(profileRepository.findByUser_Id(member.getId()))
                .thenReturn(Optional.of(MemberProfile.builder()
                        .user(member)
                        .physicalCondition("Đau đầu gối trái")
                        .build()));

        String context = service.buildMemberContext(member.getEmail(), true);

        assertThat(context).contains("Đau đầu gối trái");
    }
}
