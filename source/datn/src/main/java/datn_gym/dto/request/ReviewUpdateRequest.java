package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewUpdateRequest {

    @NotNull(message = "Số sao không được để trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    private Integer ratingStar;

    // Nhận xét là optional
    @Size(max = 2000, message = "Nhận xét không vượt quá 2000 ký tự")
    private String comment;
}
