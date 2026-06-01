package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    // true = present, false = absent
    @Column(name = "status")
    @Builder.Default
    private Boolean status = true; 

    @PrePersist
    protected void onCreate() {
        if (this.checkInTime == null) {
            this.checkInTime = LocalDateTime.now();
        }
    }
}