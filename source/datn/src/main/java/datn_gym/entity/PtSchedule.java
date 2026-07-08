package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pt_schedules",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"pt_id", "day_of_week", "time_slot"}
       ))
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

    // 0=Thứ 2, 1=Thứ 3, ..., 5=Thứ 7
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // 0-7: index của 8 slot cố định
    // 0=07-08, 1=08-09, 2=09-10, 3=13-14, 4=14-15, 5=15-16, 6=18-19, 7=19-20
    @Column(name = "time_slot", nullable = false)
    private Integer slotIndex;

    // Mô tả buổi tập (VD: "Ngực", "Chân + Core", "Vai + Tay")
    @Column(name = "exercise_note", length = 200)
    private String exerciseNote;

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

    // ===== Helper: map slotIndex → giờ cụ thể =====
    private static final String[][] SLOT_TIMES = {
        {"07:00", "08:00"}, // 0
        {"08:00", "09:00"}, // 1
        {"09:00", "10:00"}, // 2
        {"13:00", "14:00"}, // 3
        {"14:00", "15:00"}, // 4
        {"15:00", "16:00"}, // 5
        {"18:00", "19:00"}, // 6
        {"19:00", "20:00"}  // 7
    };

    private static final String[] SLOT_LABELS = {
        "Sáng", "Sáng", "Sáng",
        "Chiều", "Chiều", "Chiều",
        "Tối", "Tối"
    };

    public String getStartTime() {
        return slotIndex >= 0 && slotIndex < SLOT_TIMES.length ? SLOT_TIMES[slotIndex][0] : null;
    }

    public String getEndTime() {
        return slotIndex >= 0 && slotIndex < SLOT_TIMES.length ? SLOT_TIMES[slotIndex][1] : null;
    }

    public String getSessionLabel() {
        return slotIndex >= 0 && slotIndex < SLOT_LABELS.length ? SLOT_LABELS[slotIndex] : null;
    }
}
