package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BaiTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String ten;

    @Column(name = "muscle_group", length = 50)
    private String nhomCo;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "video_url", length = 500)
    private String duongDanVideo;

    // PT tạo bài tập này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private NguoiDung nguoiTao;
}