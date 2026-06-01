package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DiemDanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private NguoiDung hoiVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BuoiTap buoiTap;

    @Column(name = "check_in_time")
    private LocalDateTime thoiGianCheckIn;

    // true = có mặt, false = vắng
    @Column(name = "status")
    @Builder.Default
    private Boolean coMat = true;

    @PrePersist
    protected void onCreate() {
        if (this.thoiGianCheckIn == null) {
            this.thoiGianCheckIn = LocalDateTime.now();
        }
    }
}