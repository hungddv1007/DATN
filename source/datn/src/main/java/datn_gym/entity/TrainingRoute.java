package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_routes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrainingRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private User pt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User member;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "is_template")
    @Builder.Default
    private Boolean isTemplate = false;

    // DRAFT | ASSIGNED | COMPLETED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "DRAFT"; 

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
    }
}