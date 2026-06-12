package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainingRouteCreateRequest {

    @NotBlank(message = "Tên lộ trình không được để trống")
    @Size(max = 200, message = "Tên lộ trình không vượt quá 200 ký tự")
    private String name;

    // true = lưu làm template, false = lộ trình thường
    private Boolean isTemplate = false;
}
