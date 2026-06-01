package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pt_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HoSoPT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @Column(name = "specialization", length = 255)
    private String chuyenMon;

    @Column(name = "bio", columnDefinition = "NVARCHAR(MAX)")
    private String gioiThieu;

    @Column(name = "certificates", columnDefinition = "NVARCHAR(MAX)")
    private String chungChi;

    @Column(name = "rating_score", precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal diemDanhGia = BigDecimal.ZERO;
}