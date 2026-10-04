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

    // Đơn giá 1 ngày (VD: 20000 = 20k/ngày)
    @Column(name = "daily_price", nullable = false, precision = 12, scale = 0)
    private BigDecimal dailyPrice;

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

    // Số ngày đăng ký tối thiểu (VD: VIP = 30)
    @Column(name = "min_days")
    @Builder.Default
    private Integer minDays = 1;

    // Bảo lưu: số lần tối đa (0 = không cho phép)
    @Column(name = "max_hold_times")
    @Builder.Default
    private Integer maxHoldTimes = 0;

    // Bảo lưu: % ngày trả lại (VD: 80 hoặc 90)
    @Column(name = "hold_return_percent")
    @Builder.Default
    private Integer holdReturnPercent = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}