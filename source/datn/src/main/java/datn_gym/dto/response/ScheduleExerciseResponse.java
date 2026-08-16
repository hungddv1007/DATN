package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data @Builder
public class ScheduleExerciseResponse {
    private Integer exerciseId; private String exerciseName; private String muscleGroup;
    private Integer setCount; private Integer repCount; private BigDecimal weightKg;
    private Integer durationMinutes; private String note;
}
