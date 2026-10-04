package datn_gym.service;

import datn_gym.dto.response.PtProfileResponse;
import datn_gym.entity.PtProfile;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.ReviewRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PtProfileServiceTest {

    @Mock PtProfileRepository ptProfileRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock ReviewRepository reviewRepository;
    @Mock MembershipRepository membershipRepository;
    @InjectMocks PtProfileService ptProfileService;

    @Test
    void ptListIncludesCurrentAndMaximumMemberCounts() {
        User pt = User.builder().id(7).fullName("Trần Đức Việt").build();
        PtProfile profile = PtProfile.builder().id(2).user(pt).maxMembers(5).build();
        when(ptProfileRepository.findAllOrderByRatingScoreDesc()).thenReturn(List.of(profile));
        when(membershipRepository.countByPt_IdAndStatus(7, "ACTIVE")).thenReturn(5);
        when(reviewRepository.countByPt_Id(7)).thenReturn(4);

        PtProfileResponse result = ptProfileService.getAllPtProfiles().getFirst();

        assertThat(result.getTotalMembers()).isEqualTo(5);
        assertThat(result.getMaxMembers()).isEqualTo(5);
    }
}
