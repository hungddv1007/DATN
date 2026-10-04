package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ScheduleSlotResponse {
    private Integer id;
    private Integer ptId;
    private String ptName;
    private Integer memberId;
    private String memberName;
    private String scheduleDate;      // "2026-07-21"
    private String startTime;         // "14:15"
    private String endTime;           // "18:30"
    private String exerciseNote;
    private String status;
    private String actualNote;
    private java.time.LocalDateTime completedAt;
    private java.util.List<ScheduleExerciseResponse> exercises;
    private String recurringGroupId;  // UUID nhóm lặp lại
}
