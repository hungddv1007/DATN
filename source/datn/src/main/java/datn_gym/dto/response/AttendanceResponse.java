package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponse {

    private Integer id;

    // Thông tin HV
    private Integer memberId;
    private String memberName;

    // Thông tin buổi tập
    private Integer sessionId;
    private Integer weekNum;
    private Integer dayNum;
    private String sessionName;
    private Boolean isRestDay;

    // Thông tin lộ trình
    private Integer routeId;
    private String routeName;

    // Thông tin điểm danh
    private Boolean status;        // true = có mặt, false = vắng
    private LocalDateTime checkInTime;
}
