package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenewRequest {

    @NotNull(message = "Vui lòng nhập số ngày gia hạn")
    @Min(value = 1, message = "Số ngày phải >= 1")
    private Integer durationDays;

    private String promotionCode;
    private String paymentMethod;
}
