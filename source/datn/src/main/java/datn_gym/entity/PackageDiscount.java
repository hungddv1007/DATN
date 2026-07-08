package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_discounts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PackageDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // NULL = áp dụng tất cả gói tập
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private GymPackage gymPackage;

    // Đăng ký từ X ngày trở lên mới được chiết khấu
    @Column(name = "min_days", nullable = false)
    private Integer minDays;

    // % giảm giá (VD: 5, 10, 15)
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;
}
