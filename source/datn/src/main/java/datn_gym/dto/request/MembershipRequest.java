package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    private String referralCode;

    @jakarta.validation.constraints.AssertTrue(message = "Bạn phải đồng ý Điều khoản thành viên")
    private boolean acceptedTerms;

    @NotNull(message = "Thiếu phiên bản Điều khoản thành viên")
    private Integer termsVersionId;

    // CASH | BANK | MOMO
    @Pattern(regexp = "CASH|BANK|MOMO", message = "Phương thức thanh toán không hợp lệ")
    private String paymentMethod;

    // ID của PT muốn chọn (chỉ gói VIP có canChoosePt = true)
    private Integer ptId;
}
