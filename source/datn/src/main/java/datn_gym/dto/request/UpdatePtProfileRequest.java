package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePtProfileRequest {

    // Thông tin User (bảng users)

    // FIX LỖI: Tên có số/ký tự đặc biệt vẫn cập nhật được
    // \p{L}  = mọi chữ cái Unicode (bao gồm tiếng Việt có dấu: ă â ê ô ơ ư đ...)
    // \s     = khoảng trắng giữa các từ
    // '-     = cho phép tên ghép (Nguyễn-Văn) hoặc tên nước ngoài (O'Brien)
    // Không liệt kê thủ công từng ký tự có dấu để tránh thiếu sót
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    @Pattern(
        regexp = "^[\\p{L}\\s'-]+$",
        message = "Họ tên không được chứa số hoặc ký tự đặc biệt"
    )
    private String fullName;

    // Giữ nguyên pattern phone đã có từ trước
    @Pattern(
        regexp = "^(0|\\+84)[0-9]{9}$",
        message = "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 hoặc +84, 10 số)"
    )
    private String phone;

    private String avatar;

    // Thông tin chuyên môn (bảng pt_profiles)
    @Size(max = 255, message = "Chuyên môn không vượt quá 255 ký tự")
    private String specialization;

    @Size(max = 2000, message = "Tiểu sử không vượt quá 2000 ký tự")
    private String bio;

    @Size(max = 1000, message = "Chứng chỉ không vượt quá 1000 ký tự")
    private String certificates;
}