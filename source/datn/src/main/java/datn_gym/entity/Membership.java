package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "memberships")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private GymPackage gymPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id")
    private User pt;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // PENDING | ACTIVE | EXPIRED | PAUSED | CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "pause_reason", length = 255)
    private String pauseReason;

    // Snapshot: số ngày member đã đăng ký
    @Column(name = "duration_days")
    private Integer durationDays;

    // Snapshot: đơn giá tại thời điểm đăng ký
    @Column(name = "daily_price", precision = 12, scale = 0)
    private BigDecimal dailyPrice;

    // Bảo lưu: số lần đã dùng
    @Column(name = "hold_count")
    @Builder.Default
    private Integer holdCount = 0;

    // Bảo lưu: ngày bắt đầu pause
    @Column(name = "paused_at")
    private LocalDate pausedAt;

    // Bảo lưu: tổng số ngày đã bảo lưu (lũy kế)
    @Column(name = "total_hold_days")
    @Builder.Default
    private Integer totalHoldDays = 0;

    @Column(name = "hold_until")
    private LocalDate holdUntil;

    @Column(name = "hold_max_times", nullable = false)
    @Builder.Default
    private Integer holdMaxTimes = 0;

    @Column(name = "hold_max_days_per_time", nullable = false)
    @Builder.Default
    private Integer holdMaxDaysPerTime = 0;

    @Column(name = "hold_max_total_days", nullable = false)
    @Builder.Default
    private Integer holdMaxTotalDays = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
