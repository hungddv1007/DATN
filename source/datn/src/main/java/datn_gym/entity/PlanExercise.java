package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plan_exercises")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private TrainingPlan plan;

    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 0=Thứ 2, 1=Thứ 3, ..., 6=CN

    @Column(name = "week_number")
    @Builder.Default
    private Integer weekNumber = 1;
}
