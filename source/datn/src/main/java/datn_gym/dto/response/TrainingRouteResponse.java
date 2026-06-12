package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TrainingRouteResponse {

    private Integer id;
    private String name;
    private String status;
    private Boolean isTemplate;
    private LocalDate startDate;
    private LocalDateTime createdAt;

    // Thông tin PT
    private Integer ptId;
    private String ptName;

    // Thông tin HV (null nếu là template)
    private Integer memberId;
    private String memberName;

    // Thống kê nhanh
    private Integer totalWeeks;
    private Integer totalSessions;   // Tổng buổi tập (không tính rest day)

    // Cấu trúc lồng nhau: Map<tuần, danh sách buổi>
    // VD: {1: [Mon, Tue, ...], 2: [Mon, Tue, ...]}
    private Map<Integer, List<SessionResponse>> weeks;
}
