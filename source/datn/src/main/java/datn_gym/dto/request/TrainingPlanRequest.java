package datn_gym.dto.request;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TrainingPlanRequest {
    private String title;
    private String description;
    private Integer durationWeeks;
    private String difficulty;
    private String goal;
    private Boolean isTemplate;
    private List<PlanExerciseRequest> exercises;
}
