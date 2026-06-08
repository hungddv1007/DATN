package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePtProfileRequest {

    // Thông tin User (bảng users)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ (Phải bắt đầu bằng 0 hoặc +84, từ 10-11 số)")
    private String phone;

    private String avatar;

    // Thông tin chuyên môn (bảng pt_profiles)
    @Size(max = 255, message = "Chuyên môn không vượt quá 255 ký tự")
    private String specialization;

    @Size(max = 2000, message = "Tiểu sử không vượt quá 2000 ký tự")
    private String bio;

    private String certificates;
}