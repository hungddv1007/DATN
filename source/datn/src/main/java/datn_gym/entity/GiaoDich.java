package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private HoiVienGoi hoiVienGoi;

    // NULL = không dùng khuyến mãi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private KhuyenMai khuyenMai;

    @Column(name = "amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal soTien;

    // Giá gốc trước khi áp mã KM
    @Column(name = "original_amount", precision = 12, scale = 0)
    private BigDecimal soTienGoc;

    // CASH | BANK | ONLINE
    @Column(name = "payment_method", length = 20)
    private String phuongThucThanhToan;

    // PENDING | CONFIRMED | CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String trangThai = "PENDING";

    // Admin xác nhận thanh toán
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private NguoiDung nguoiXacNhan;

    @Column(name = "created_at")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}