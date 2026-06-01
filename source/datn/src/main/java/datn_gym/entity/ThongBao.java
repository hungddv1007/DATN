package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Người nhận thông báo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private NguoiDung nguoiNhan;

    // NULL = thông báo hệ thống (Admin gửi)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private NguoiDung nguoiGui;

    @Column(name = "title", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "message", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean daDoc = false;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}