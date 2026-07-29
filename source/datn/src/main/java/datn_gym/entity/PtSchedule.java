package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pt_schedules")
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

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Mô tả buổi tập (VD: "Ngực", "Chân + Core", "Vai + Tay")
    @Column(name = "exercise_note", length = 200)
    private String exerciseNote;

    // UUID nhóm lặp lại — NULL nếu tạo đơn lẻ
    @Column(name = "recurring_group_id", length = 36)
    private String recurringGroupId;

    // ACTIVE | CANCELLED
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
