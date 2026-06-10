package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Integer id;

    // Thông tin hội viên đánh giá
    private Integer memberId;
    private String memberName;
    private String memberAvatar;

    // Thông tin PT được đánh giá
    private Integer ptId;
    private String ptName;

    private Integer ratingStar;
    private String comment;
    private LocalDateTime createdAt;
}
