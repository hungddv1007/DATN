package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainingRouteUpdateRequest {

    @NotBlank(message = "Tên lộ trình không được để trống")
    @Size(max = 200, message = "Tên lộ trình không vượt quá 200 ký tự")
    private String name;
}
