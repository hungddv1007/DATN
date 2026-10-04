package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class SaleCodeResponse {
    private Integer id; private String code; private String description; private Integer discountPercent;
    private boolean oneTimePerMember; private boolean active; private LocalDateTime expiresAt; private LocalDateTime createdAt;
}
