package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HoSoHoiVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    // PT ghi khi đánh giá thể chất ban đầu
    @Column(name = "physical_condition", columnDefinition = "NVARCHAR(MAX)")
    private String tinhTrangTheLuc;
}