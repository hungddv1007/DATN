package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PtCommentResponse {

    private Integer id;

    // Thông tin PT gửi nhận xét
    private Integer ptId;
    private String ptName;
    private String ptAvatar;

    // Thông tin hội viên nhận nhận xét
    private Integer memberId;
    private String memberName;

    // Lộ trình gắn kèm (có thể null = nhận xét chung)
    private Integer routeId;
    private String routeName;

    private String content;
    private LocalDateTime createdAt;
}
