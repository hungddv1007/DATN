package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipRequest {

    @NotNull(message = "Vui lòng chọn gói tập")
    private Integer packageId;

    @NotNull(message = "Vui lòng nhập số ngày đăng ký")
    @Min(value = 1, message = "Số ngày phải >= 1")
    private Integer durationDays;

    // Mã khuyến mãi (tùy chọn)
    private String promotionCode;

    // CASH | BANK | ONLINE
    private String paymentMethod;

    // ID của PT muốn chọn (chỉ gói VIP có canChoosePt = true)
    private Integer ptId;
}
