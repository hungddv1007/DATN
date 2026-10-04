package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiConsentRequest {

    @NotNull(message = "Vui lòng chọn trạng thái đồng ý")
    private Boolean consent;
}
