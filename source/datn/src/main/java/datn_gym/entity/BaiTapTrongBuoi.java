package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "session_exercises")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BaiTapTrongBuoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ON DELETE CASCADE — xóa buổi tập → xóa tất cả bài tập trong buổi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BuoiTap buoiTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private BaiTap baiTap;

    @Column(name = "sets")
    @Builder.Default
    private Integer soSet = 3;

    @Column(name = "reps")
    @Builder.Default
    private Integer soLanLap = 10;

    // NULL = bài tập không dùng tạ (bodyweight)
    @Column(name = "weight_kg", precision = 5, scale = 1)
    private BigDecimal canNangKg;

    @Column(name = "notes", length = 255)
    private String ghiChu;
}