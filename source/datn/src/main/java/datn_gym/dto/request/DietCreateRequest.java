package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DietCreateRequest {

    @NotNull(message = "Member ID không được để trống")
    private Integer memberId;

    @NotNull(message = "Ngày không được để trống")
    private LocalDate date;

    // Ít nhất 1 trong 3 bữa phải có nội dung — validate trong Service
    @Size(max = 1000, message = "Bữa sáng không vượt quá 1000 ký tự")
    private String breakfast;

    @Size(max = 1000, message = "Bữa trưa không vượt quá 1000 ký tự")
    private String lunch;

    @Size(max = 1000, message = "Bữa tối không vượt quá 1000 ký tự")
    private String dinner;
}
