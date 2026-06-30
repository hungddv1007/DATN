package datn_gym.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrainingPlanResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer durationWeeks;
    private String difficulty;
    private String goal;
    private Boolean isTemplate;
    private LocalDateTime createdAt;
    private Integer totalExercises;
    private Long activeAssignments;
    private Long totalAssignments;
    private List<PlanExerciseResponse> exercises;
}
