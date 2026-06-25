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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PtDashboardService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final ReviewRepository reviewRepository;

    public PtDashboardResponse getDashboardStats(String email) {
        User pt = getUserByEmail(email);

        long activeMembers = membershipRepository.countByPt_IdAndStatus(pt.getId(), "ACTIVE");
        long totalTemplates = 0L;
        long totalReviews = reviewRepository.countByPt_Id(pt.getId());

        return PtDashboardResponse.builder()
                .activeMembers(activeMembers)
                .totalTemplates(totalTemplates)
                .totalReviews(totalReviews)
                .build();
    }

    public List<PtMemberResponse> getAssignedMembers(String email) {
        User pt = getUserByEmail(email);
        List<Membership> memberships = membershipRepository.findByPt_IdAndStatus(pt.getId(), "ACTIVE");

        return memberships.stream().map(m -> PtMemberResponse.builder()
                .memberId(m.getUser().getId())
                .memberName(m.getUser().getFullName())
                .memberEmail(m.getUser().getEmail())
                .memberPhone(m.getUser().getPhone())
                .membershipId(m.getId())
                .packageName(m.getGymPackage().getName())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .build()
        ).collect(Collectors.toList());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }
}
