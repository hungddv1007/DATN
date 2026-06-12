package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SessionExerciseRequest {

    @NotNull(message = "Exercise ID không được để trống")
    private Integer exerciseId;

    @Min(value = 1, message = "Số set tối thiểu là 1")
    @Max(value = 100, message = "Số set tối đa là 100")
    private Integer sets = 3;

    @Min(value = 1, message = "Số rep tối thiểu là 1")
    @Max(value = 1000, message = "Số rep tối đa là 1000")
    private Integer reps = 10;

    // Nullable — bài tập bodyweight không cần tạ
    @DecimalMin(value = "0.0", message = "Cân nặng không được âm")
    @DecimalMax(value = "999.9", message = "Cân nặng tối đa 999.9kg")
    private BigDecimal weightKg;

    @Size(max = 255, message = "Ghi chú không vượt quá 255 ký tự")
    private String notes;
}
