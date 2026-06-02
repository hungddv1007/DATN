package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
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

    // ACTIVE | EXPIRED | PAUSED | CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE"; 

    @Column(name = "pause_reason", length = 255)
    private String pauseReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
    }
}