package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "session_exercises")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "sets")
    @Builder.Default
    private Integer sets = 3;

    @Column(name = "reps")
    @Builder.Default
    private Integer reps = 10;

    @Column(name = "weight_kg", precision = 5, scale = 1)
    private BigDecimal weightKg;

    @Column(name = "notes", length = 255)
    private String notes;
}