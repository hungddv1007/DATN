package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class ServiceReviewResponse {
    private Integer id; private Integer memberId; private String memberName; private String memberAvatar;
    private String packageName; private Integer transactionId; private Integer ratingStar; private String comment;
    private boolean displayName; private boolean featured; private LocalDateTime createdAt;
}
