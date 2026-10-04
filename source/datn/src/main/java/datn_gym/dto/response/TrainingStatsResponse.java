package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;
@Data @Builder
public class TrainingStatsResponse {
    private Integer memberId; private String memberName; private String fromDate; private String toDate;
    private long scheduledSessions; private long completedSessions; private long cancelledSessions; private long noShowSessions;
    private long completedMinutes; private Map<String, Long> muscleGroupFrequency;
    private Map<String, Long> exerciseFrequency; private List<ScheduleSlotResponse> sessions;
}
