package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private GymPackage gymPackage;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "current_usage")
    @Builder.Default
    private Integer currentUsage = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}