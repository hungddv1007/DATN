package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BuoiTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ON DELETE CASCADE — xóa lộ trình → xóa tất cả buổi tập
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private LoTrinhTapLuyen loTrinh;

    @Column(name = "week_num", nullable = false)
    private Integer tuan;

    @Column(name = "day_num", nullable = false)
    private Integer ngayTrongTuan;

    // "Chest Day", "Rest Day", v.v.
    @Column(name = "name", length = 100)
    private String ten;

    @Column(name = "is_rest_day")
    @Builder.Default
    private Boolean laNgayNghi = false;
}