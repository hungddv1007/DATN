package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExerciseRequest {
    @NotBlank(message = "Tên bài tập không được để trống")
    private String name;

    private String muscleGroup;
    private String description;
    private String videoUrl;
}
