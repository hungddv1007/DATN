package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_hold_policies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PackageHoldPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private GymPackage gymPackage;

    @Column(name = "min_duration_days", nullable = false)
    private Integer minDurationDays;

    @Column(name = "max_duration_days")
    private Integer maxDurationDays;

    @Column(name = "max_hold_times", nullable = false)
    private Integer maxHoldTimes;

    @Column(name = "max_days_per_hold", nullable = false)
    private Integer maxDaysPerHold;

    @Column(name = "max_total_hold_days", nullable = false)
    private Integer maxTotalHoldDays;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
