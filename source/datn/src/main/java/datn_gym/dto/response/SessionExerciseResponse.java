package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SessionExerciseResponse {
    private Integer id;

    // Thông tin bài tập
    private Integer exerciseId;
    private String exerciseName;
    private String muscleGroup;
    private String videoUrl;

    // Thông số tập luyện
    private Integer sets;
    private Integer reps;
    private BigDecimal weightKg;
    private String notes;
}
