package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Response gọn — dùng cho danh sách, không load sessions
// Tránh load toàn bộ dữ liệu khi chỉ cần xem danh sách
@Data
@Builder
public class TrainingRouteSummaryResponse {
    private Integer id;
    private String name;
    private String status;
    private Boolean isTemplate;
    private LocalDate startDate;
    private LocalDateTime createdAt;

    private Integer ptId;
    private String ptName;

    private Integer memberId;
    private String memberName;

    private Integer totalWeeks;
    private Integer totalSessions;
}
