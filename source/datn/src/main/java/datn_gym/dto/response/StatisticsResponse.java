package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse {
    private long totalUsers;
    private long newRegistrationsThisMonth;
    private long monthlyRevenue;
    private long activePTs;
}
