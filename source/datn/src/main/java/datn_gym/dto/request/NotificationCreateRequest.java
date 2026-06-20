package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NotificationCreateRequest {

    @NotNull(message = "User ID không được để trống")
    private Integer userId;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không vượt quá 200 ký tự")
    private String title;

    @Size(max = 3000, message = "Nội dung không vượt quá 3000 ký tự")
    private String message;
}
