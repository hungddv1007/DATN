package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GymPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "has_pt")
    @Builder.Default
    private Boolean hasPt = false;

    @Column(name = "can_choose_pt")
    @Builder.Default
    private Boolean canChoosePt = false;

    @Column(name = "has_meal_plan")
    @Builder.Default
    private Boolean hasMealPlan = false;
}