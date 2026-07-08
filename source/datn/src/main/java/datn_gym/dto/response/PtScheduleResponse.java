package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PtScheduleResponse {
    private Integer id;
    private Integer ptId;
    private String ptName;
    private Integer memberId;
    private String memberName;
    private String memberAvatar;
    private Integer dayOfWeek;
    private String timeSlot;
    private String status;
    private LocalDateTime createdAt;
}
