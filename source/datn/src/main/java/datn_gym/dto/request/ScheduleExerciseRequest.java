package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScheduleExerciseRequest {
    @NotNull private Integer exerciseId;
    @Min(1) private Integer setCount;
    @Min(1) private Integer repCount;
    @DecimalMin("0.0") private BigDecimal weightKg;
    @Min(1) private Integer durationMinutes;
    @Size(max = 500) private String note;
}
