package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HumanChatMessageRequest {
    @NotBlank @Size(max = 2000) private String message;
}
