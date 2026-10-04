package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MembershipTransferAcceptRequest {
    @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP phải gồm 6 chữ số") private String otp;
    @NotNull(message = "Thiếu phiên bản chính sách chuyển nhượng")
    private Integer transferPolicyVersionId;
    @AssertTrue(message = "Bạn phải đồng ý chính sách chuyển nhượng")
    private boolean acceptedPolicy;
    private boolean confirmedReplacement;
}
