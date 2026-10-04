package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PtNoteResponse {

    private Integer id;

    // Thông tin PT viết ghi chú
    private Integer ptId;
    private String ptName;

    // Thông tin hội viên được ghi chú
    private Integer memberId;
    private String memberName;

    private String content;
    private LocalDateTime createdAt;
}