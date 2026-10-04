package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtDashboardResponse {
    private long totalAssignedMembers;
    private long activeMembers;
    private long totalTemplates;
    private long totalReviews;
    private long todaySessions;
}
