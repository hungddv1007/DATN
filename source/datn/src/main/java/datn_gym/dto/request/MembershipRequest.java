package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipRequest {

    @NotNull(message = "Vui lòng chọn gói tập")
    private Integer packageId;

    // Số ngày đăng ký / gia hạn
    private Integer durationDays;

    // ID gói tập mới (dùng khi nâng cấp)
    private Integer newPackageId;

    // Mã khuyến mãi (tùy chọn)
    private String promotionCode;

    // CASH | BANK | ONLINE
    private String paymentMethod;

    // ID của PT muốn chọn (chỉ dùng cho gói có canChoosePt = true)
    private Integer ptId;
}
