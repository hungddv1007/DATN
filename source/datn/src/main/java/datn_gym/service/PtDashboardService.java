package datn_gym.service;

import datn_gym.dto.response.PtDashboardResponse;
import datn_gym.dto.response.PtMemberResponse;
import datn_gym.entity.Membership;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.ReviewRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtDashboardService {

    private final UserService userService;
    private final MembershipRepository membershipRepository;
    private final ReviewRepository reviewRepository;
    private final datn_gym.repository.PtScheduleRepository ptScheduleRepository;

    public PtDashboardResponse getDashboardStats(String email) {
        User pt = userService.getUserByEmail(email);

        long activeMembers = membershipRepository.countByPt_IdAndStatus(pt.getId(), "ACTIVE");
        long totalTemplates = 0L;
        long totalReviews = reviewRepository.countByPt_Id(pt.getId());
        long todaySessions = ptScheduleRepository.countTodaySessions(
                pt.getId(),
                LocalDate.now(),
                List.of("SCHEDULED", "COMPLETED"));

        return PtDashboardResponse.builder()
                .activeMembers(activeMembers)
                .totalTemplates(totalTemplates)
                .totalReviews(totalReviews)
                .todaySessions(todaySessions)
                .build();
    }

    public List<PtMemberResponse> getAssignedMembers(String email) {
        User pt = userService.getUserByEmail(email);
        List<Membership> memberships = membershipRepository.findByPtIdAndStatusWithDetails(pt.getId(), "ACTIVE");

        return memberships.stream().map(m -> {
            boolean isScheduled = ptScheduleRepository.existsActiveMemberSchedule(m.getUser().getId());
            return PtMemberResponse.builder()
                    .memberId(m.getUser().getId())
                    .memberName(m.getUser().getFullName())
                    .memberEmail(m.getUser().getEmail())
                    .memberPhone(m.getUser().getPhone())
                    .membershipId(m.getId())
                    .packageName(m.getGymPackage().getName())
                    .startDate(m.getStartDate())
                    .endDate(m.getEndDate())
                    .status(m.getStatus())
                    .isScheduled(isScheduled)
                    .build();
        }).collect(Collectors.toList());
    }


}
