package datn_gym.service;

import datn_gym.dto.request.CreateScheduleRequest;
import datn_gym.entity.User;
import datn_gym.repository.ExerciseRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PtScheduleServiceTest {

    @Mock PtScheduleRepository ptScheduleRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock MembershipRepository membershipRepository;
    @Mock NotificationService notificationService;
    @Mock ExerciseRepository exerciseRepository;
    @InjectMocks PtScheduleService ptScheduleService;

    @Test
    void createRecurringScheduleRejectsMoreThanFifteenWeeks() {
        User pt = User.builder().id(1).email("pt@gympro.com").build();
        User member = User.builder().id(2).fullName("Hội viên").build();
        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setMemberId(member.getId());
        request.setScheduleDate(LocalDate.of(2026, 9, 5));
        request.setStartTime(LocalTime.of(8, 0));
        request.setEndTime(LocalTime.of(9, 0));
        request.setRecurring(true);
        request.setRecurringWeeks(16);

        when(userService.getUserByEmail("pt@gympro.com")).thenReturn(pt);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(membershipRepository.existsActiveMembershipByPtAndMember(pt.getId(), member.getId()))
                .thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ptScheduleService.createSchedule("pt@gympro.com", request));

        assertEquals("Lịch lặp phải kéo dài từ 2 đến tối đa 15 tuần", error.getMessage());
        verify(ptScheduleRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
