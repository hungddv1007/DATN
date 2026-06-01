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
public class LoTrinhTapLuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // PT tạo lộ trình
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private NguoiDung pt;

    // NULL = lộ trình template (chưa giao cho ai)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private NguoiDung hoiVien;

    @Column(name = "name", nullable = false, length = 200)
    private String ten;

    // true = template có thể clone
    @Column(name = "is_template")
    @Builder.Default
    private Boolean laTemplate = false;

    // DRAFT | ASSIGNED | COMPLETED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String trangThai = "DRAFT";

    @Column(name = "start_date")
    private LocalDate ngayBatDau;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}