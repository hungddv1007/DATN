package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SessionCreateRequest {

    @NotNull(message = "Số tuần không được để trống")
    @Min(value = 1, message = "Số tuần tối thiểu là 1")
    private Integer weekNum;

    @NotNull(message = "Số ngày không được để trống")
    @Min(value = 1, message = "Số ngày tối thiểu là 1")
    @Max(value = 7, message = "Số ngày tối đa là 7")
    private Integer dayNum;

    @Size(max = 100, message = "Tên buổi không vượt quá 100 ký tự")
    private String name;

    private Boolean isRestDay = false;
}
