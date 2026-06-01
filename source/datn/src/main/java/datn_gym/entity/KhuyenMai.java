package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String maCode;

    @Column(name = "discount_percent", nullable = false)
    private Integer phanTramGiam;

    // NULL = áp dụng tất cả gói
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private GoiTap goiTap;

    @Column(name = "start_date", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "end_date", nullable = false)
    private LocalDate ngayKetThuc;

    // NULL = không giới hạn số lần dùng
    @Column(name = "max_usage")
    private Integer soLanToiDa;

    @Column(name = "current_usage")
    @Builder.Default
    private Integer soLanDaDung = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean dangHoatDong = true;
}