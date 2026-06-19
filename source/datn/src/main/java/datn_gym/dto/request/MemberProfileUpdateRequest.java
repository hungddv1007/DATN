package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberProfileUpdateRequest {

    // PT ghi nhận tình trạng thể chất ban đầu / cập nhật theo tiến trình
    @NotBlank(message = "Tình trạng thể chất không được để trống")
    @Size(max = 3000, message = "Tình trạng thể chất không vượt quá 3000 ký tự")
    private String physicalCondition;
}
