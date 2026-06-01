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
public class HoiVienGoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private NguoiDung hoiVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private GoiTap goiTap;

    // NULL = gói Basic (không có PT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id")
    private NguoiDung pt;

    @Column(name = "start_date", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "end_date", nullable = false)
    private LocalDate ngayKetThuc;

    // ACTIVE | EXPIRED | PAUSED | CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String trangThai = "ACTIVE";

    @Column(name = "pause_reason", length = 255)
    private String lyDoTamDung;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}