package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSaleAccountRequest {
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank @Size(max = 100) private String fullName;
    @Pattern(regexp = "^$|^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0")
    private String phone;
}
