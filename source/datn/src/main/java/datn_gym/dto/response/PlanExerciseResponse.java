package datn_gym.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlanExerciseResponse {
    private Integer id;
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Integer restSeconds;
    private Integer dayOfWeek;
    private Integer weekNumber;
}
