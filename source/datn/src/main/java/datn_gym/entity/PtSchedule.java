package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pt_schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pt_id", "day_of_week", "time_slot"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PtSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private User pt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    // 0 = Thứ 2, 1 = Thứ 3, ..., 6 = Chủ Nhật
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // MORNING, AFTERNOON, EVENING
    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot;

    // ACTIVE, CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
