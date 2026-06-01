package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BaiViet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // PT hoặc Admin viết bài
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private NguoiDung tacGia;

    @Column(name = "title", nullable = false, length = 300)
    private String tieuDe;

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "thumbnail", length = 500)
    private String anhDaiDien;

    // DRAFT | PUBLISHED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String trangThai = "PUBLISHED";

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}