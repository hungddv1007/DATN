package datn_gym.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PlanExerciseRequest {
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Integer restSeconds;
    private Integer dayOfWeek;
    private Integer weekNumber;
}
