package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class RenewRequest {

    @NotNull(message = "Vui lòng nhập số ngày gia hạn")
    @Min(value = 1, message = "Số ngày phải >= 1")
    private Integer durationDays;

    private String promotionCode;
    @Pattern(regexp = "CASH|BANK|MOMO", message = "Phương thức thanh toán không hợp lệ")
    private String paymentMethod;
}
