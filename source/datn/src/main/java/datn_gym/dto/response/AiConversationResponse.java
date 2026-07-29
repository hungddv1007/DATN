package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AiConversationResponse {
    private Integer id;
    private String title;
    private boolean physicalDataConsent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
