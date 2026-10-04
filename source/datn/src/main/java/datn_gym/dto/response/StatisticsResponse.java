package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse {
    private long totalUsers;
    private long transactionsThisMonth;
    private long monthlyRevenue;
    private long activePTs;

    private java.util.List<ChartData> revenueData;
    private java.util.List<ChartData> packageData;

    @Data
    @Builder
    public static class ChartData {
        private String name;
        private long value;
    }
}
