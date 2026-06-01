package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "diets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KhauPhanAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Hội viên được lên thực đơn (chỉ VIP)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private NguoiDung hoiVien;

    // PT lên thực đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private NguoiDung pt;

    @Column(name = "date", nullable = false)
    private LocalDate ngay;

    @Column(name = "breakfast", columnDefinition = "NVARCHAR(MAX)")
    private String buoiSang;

    @Column(name = "lunch", columnDefinition = "NVARCHAR(MAX)")
    private String buoiTrua;

    @Column(name = "dinner", columnDefinition = "NVARCHAR(MAX)")
    private String buoiToi;
}