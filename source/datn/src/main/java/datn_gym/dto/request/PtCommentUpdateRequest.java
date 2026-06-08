package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PtCommentUpdateRequest {

    @NotBlank(message = "Nội dung nhận xét không được để trống")
    @Size(min = 1, max = 5000, message = "Nội dung phải từ 1 đến 5000 ký tự")
    private String content;
}
