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
    private Integer dayOfWeek;        // 0-5
    private Integer slotIndex;        // 0-7
    private String startTime;         // "07:00"
    private String endTime;           // "08:00"
    private String sessionLabel;      // "Sáng", "Chiều", "Tối"
    private String exerciseNote;      // "Ngực", "Chân + Core"
    private String status;
}
