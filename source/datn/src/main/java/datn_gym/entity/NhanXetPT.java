package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pt_comments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NhanXetPT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private NguoiDung pt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private NguoiDung hoiVien;

    // NULL = nhận xét chung, không gắn với lộ trình cụ thể
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private LoTrinhTapLuyen loTrinh;

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}