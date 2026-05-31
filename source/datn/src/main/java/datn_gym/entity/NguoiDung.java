package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private VaiTro vaiTro;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "full_name", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "phone", length = 20)
    private String soDienThoai;

    @Column(name = "avatar", length = 500)
    private String anhDaiDien;

    @Column(name = "status")
    private Boolean trangThai = true;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}
