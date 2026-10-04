package datn_gym.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class ChatHandoffRequest {
    @AssertTrue(message = "Bạn phải đồng ý chia sẻ cuộc trò chuyện với nhân viên tư vấn")
    private boolean consent;
}
