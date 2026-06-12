package datn_gym.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DietUpdateRequest {

    // Tách riêng UpdateRequest — không cần memberId và date khi update
    // Áp dụng nguyên tắc Single Responsibility đã học từ PtComment

    @Size(max = 1000, message = "Bữa sáng không vượt quá 1000 ký tự")
    private String breakfast;

    @Size(max = 1000, message = "Bữa trưa không vượt quá 1000 ký tự")
    private String lunch;

    @Size(max = 1000, message = "Bữa tối không vượt quá 1000 ký tự")
    private String dinner;
}
