package datn_gym.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiConversationCreateRequest {

    @Size(max = 120, message = "Tiêu đề hội thoại không được vượt quá 120 ký tự")
    private String title;
}
