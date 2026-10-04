package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "member_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "biological_sex", length = 10)
    private String biologicalSex;

    @Column(name = "chest_cm", precision = 5, scale = 2)
    private BigDecimal chestCm;

    @Column(name = "waist_cm", precision = 5, scale = 2)
    private BigDecimal waistCm;

    @Column(name = "hip_cm", precision = 5, scale = 2)
    private BigDecimal hipCm;

    @Column(name = "body_fat_percentage", precision = 5, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(name = "body_fat_source", length = 10)
    private String bodyFatSource;

    @Column(name = "activity_level", length = 30)
    private String activityLevel;

    @Column(name = "fitness_goal", length = 30)
    private String fitnessGoal;

    @Column(name = "target_weight_kg", precision = 6, scale = 2)
    private BigDecimal targetWeightKg;

    @Column(name = "training_experience", length = 500)
    private String trainingExperience;

    @Column(name = "injury_history", columnDefinition = "NVARCHAR(2000)")
    private String injuryHistory;

    @Column(name = "medical_conditions", columnDefinition = "NVARCHAR(2000)")
    private String medicalConditions;
}
