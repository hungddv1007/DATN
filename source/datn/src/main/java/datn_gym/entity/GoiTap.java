package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "packages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GoiTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String ten;

    @Column(name = "price", nullable = false, precision = 12, scale = 0)
    private BigDecimal gia;

    @Column(name = "duration_days", nullable = false)
    private Integer soNgay;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    // Có PT kèm không (Premium, VIP = true)
    @Column(name = "has_pt")
    @Builder.Default
    private Boolean coPT = false;

    // Được chọn PT không (VIP = true)
    @Column(name = "can_choose_pt")
    @Builder.Default
    private Boolean duocChonPT = false;

    // Có khẩu phần ăn không (VIP = true)
    @Column(name = "has_meal_plan")
    @Builder.Default
    private Boolean coKhauPhanAn = false;
}