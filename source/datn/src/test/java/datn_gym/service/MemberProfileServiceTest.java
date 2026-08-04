package datn_gym.service;

import datn_gym.dto.request.MemberProfileUpdateRequest;
import datn_gym.dto.response.MemberProfileResponse;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.Role;
import datn_gym.entity.User;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberProfileRepository memberProfileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private MembershipRepository membershipRepository;

    private MemberProfileService service;

    @BeforeEach
    void setUp() {
        service = new MemberProfileService(
                memberProfileRepository,
                userRepository,
                userService,
                membershipRepository);
    }

    @Test
    void memberCanCreateOwnPartialPhysicalProfile() {
        User member = user(20, "member@gym.local", "MEMBER");
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest();
        request.setHeightCm(new BigDecimal("172.5"));
        request.setFitnessGoal("MUSCLE_GAIN");
        request.setMedicalConditions("   ");
        when(userService.getUserByEmail(member.getEmail())).thenReturn(member);
        when(memberProfileRepository.findByUser_Id(member.getId()))
                .thenReturn(Optional.empty());
        when(memberProfileRepository.save(any(MemberProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MemberProfileResponse response =
                service.updateMyProfile(member.getEmail(), request);

        assertThat(response.getHeightCm()).isEqualByComparingTo("172.5");
        assertThat(response.getFitnessGoal()).isEqualTo("MUSCLE_GAIN");
        assertThat(response.getMedicalConditions()).isNull();
        ArgumentCaptor<MemberProfile> profileCaptor =
                ArgumentCaptor.forClass(MemberProfile.class);
        verify(memberProfileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getUser()).isEqualTo(member);
    }

    @Test
    void assignedPtCanViewMemberEvenWhenPhysicalProfileDoesNotExist() {
        User pt = user(10, "pt@gym.local", "PT");
        User member = user(20, "member@gym.local", "MEMBER");
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), member.getId())).thenReturn(true);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberProfileRepository.findByUser_Id(member.getId()))
                .thenReturn(Optional.empty());

        MemberProfileResponse response =
                service.getMemberProfile(pt.getEmail(), member.getId());

        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getHeightCm()).isNull();
        assertThat(response.getMedicalConditions()).isNull();
    }

    @Test
    void bodyFatIsEstimatedWhenAdultProfileHasAllRequiredInputs() {
        User member = user(20, "member@gym.local", "MEMBER");
        MemberProfileUpdateRequest request = new MemberProfileUpdateRequest();
        request.setHeightCm(new BigDecimal("172"));
        request.setWeightKg(new BigDecimal("70"));
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setBiologicalSex("MALE");
        request.setBodyFatPercentage(new BigDecimal("40"));
        when(userService.getUserByEmail(member.getEmail())).thenReturn(member);
        when(memberProfileRepository.findByUser_Id(member.getId()))
                .thenReturn(Optional.empty());
        when(memberProfileRepository.save(any(MemberProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MemberProfileResponse response =
                service.updateMyProfile(member.getEmail(), request);

        assertThat(response.getBodyFatSource()).isEqualTo("ESTIMATED");
        assertThat(response.getBodyFatPercentage())
                .isNotEqualByComparingTo("40")
                .isBetween(new BigDecimal("15"), new BigDecimal("25"));
    }

    @Test
    void ptCannotViewPhysicalProfileOfUnassignedMember() {
        User pt = user(10, "pt@gym.local", "PT");
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), 20)).thenReturn(false);

        assertThatThrownBy(() -> service.getMemberProfile(pt.getEmail(), 20))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));

        verify(userRepository, never()).findById(20);
        verify(memberProfileRepository, never()).findByUser_Id(20);
    }

    private User user(int id, String email, String roleName) {
        return User.builder()
                .id(id)
                .email(email)
                .fullName(email)
                .role(Role.builder().name(roleName).build())
                .build();
    }
}
