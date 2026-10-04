package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class PolicyVersionResponse {
    private Integer id; private String policyType; private Integer versionNumber;
    private String title; private String content; private LocalDateTime effectiveAt;
}
