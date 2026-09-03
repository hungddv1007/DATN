package datn_gym.service;

import datn_gym.dto.response.DietResponse;
import datn_gym.entity.Diet;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock DietRepository dietRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock MembershipRepository membershipRepository;
    @Mock PtScheduleRepository ptScheduleRepository;
    @InjectMocks DietService dietService;

    @Test
    void scheduledSessionUsesTrainingDayDiet() {
        LocalDate date = LocalDate.of(2026, 9, 3);
        User member = User.builder().id(29).email("member29@gympro.com").build();
        User pt = User.builder().id(8).fullName("PT Test").build();
        Diet trainingDiet = Diet.builder().id(1).member(member).pt(pt).dayType("TRAINING_DAY").build();
        PtSchedule schedule = PtSchedule.builder().status("SCHEDULED").scheduleDate(date).build();
        prepareDietLookup(member, date, "TRAINING_DAY", trainingDiet);
        when(ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusInOrderByScheduleDateAscStartTimeAsc(
                        29, date, date, List.of("SCHEDULED", "COMPLETED")))
                .thenReturn(List.of(schedule));

        DietResponse result = dietService.getMyDietForDate(member.getEmail(), date);

        assertTrue(result.getIsTrainingDay());
        verify(ptScheduleRepository)
                .findByMemberIdAndScheduleDateBetweenAndStatusInOrderByScheduleDateAscStartTimeAsc(
                        29, date, date, List.of("SCHEDULED", "COMPLETED"));
    }

    @Test
    void completedSessionStillUsesTrainingDayDiet() {
        LocalDate date = LocalDate.of(2026, 9, 2);
        User member = User.builder().id(29).email("member29@gympro.com").build();
        User pt = User.builder().id(8).fullName("PT Test").build();
        Diet trainingDiet = Diet.builder().id(1).member(member).pt(pt).dayType("TRAINING_DAY").build();
        PtSchedule schedule = PtSchedule.builder().status("COMPLETED").scheduleDate(date).build();
        prepareDietLookup(member, date, "TRAINING_DAY", trainingDiet);
        when(ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusInOrderByScheduleDateAscStartTimeAsc(
                        29, date, date, List.of("SCHEDULED", "COMPLETED")))
                .thenReturn(List.of(schedule));

        DietResponse result = dietService.getMyDietForDate(member.getEmail(), date);

        assertTrue(result.getIsTrainingDay());
    }

    @Test
    void cancelledOrNoShowSessionUsesRestDayDiet() {
        LocalDate date = LocalDate.of(2026, 9, 4);
        User member = User.builder().id(29).email("member29@gympro.com").build();
        User pt = User.builder().id(8).fullName("PT Test").build();
        Diet restDiet = Diet.builder().id(2).member(member).pt(pt).dayType("REST_DAY").build();
        prepareDietLookup(member, date, "REST_DAY", restDiet);
        when(ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusInOrderByScheduleDateAscStartTimeAsc(
                        29, date, date, List.of("SCHEDULED", "COMPLETED")))
                .thenReturn(List.of());

        DietResponse result = dietService.getMyDietForDate(member.getEmail(), date);

        assertFalse(result.getIsTrainingDay());
    }

    private void prepareDietLookup(User member, LocalDate date, String dayType, Diet diet) {
        when(userService.getUserByEmail(member.getEmail())).thenReturn(member);
        when(dietRepository.findByMember_IdAndDayTypeAndDietDate(member.getId(), "SPECIFIC_DATE", date))
                .thenReturn(Optional.empty());
        when(dietRepository.findByMember_IdAndDayType(member.getId(), dayType))
                .thenReturn(Optional.of(diet));
    }
}
