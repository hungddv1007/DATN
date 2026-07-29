package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AiMessageResponse {
    private Long id;
    private String role;
    private String content;
    private String model;
    private LocalDateTime createdAt;
}
