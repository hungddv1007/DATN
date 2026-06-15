package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseResponse {
    private Integer id;
    private String name;
    private String muscleGroup;
    private String description;
    private String videoUrl;
    private String createdBy;
}
