package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NotificationBulkCreateRequest {

    @NotEmpty(message = "Danh sách người nhận không được để trống")
    @Size(max = 500, message = "Mỗi lần chỉ được gửi tối đa 500 người nhận")
    private List<
            @NotNull(message = "ID người nhận không hợp lệ")
            @Positive(message = "ID người nhận không hợp lệ") Integer> userIds;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không vượt quá 200 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 3000, message = "Nội dung không vượt quá 3000 ký tự")
    private String message;
}
