package datn_gym.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PauseMembershipRequest {
    @NotNull(message = "Vui lòng chọn số ngày bảo lưu")
    @Min(value = 1, message = "Số ngày bảo lưu phải lớn hơn 0")
    @Max(value = 365, message = "Số ngày bảo lưu không hợp lệ")
    private Integer days;
    private String reason;
}
