package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private TrainingRoute route;

    @Column(name = "week_num", nullable = false)
    private Integer weekNum;

    @Column(name = "day_num", nullable = false)
    private Integer dayNum;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "is_rest_day")
    @Builder.Default
    private Boolean isRestDay = false;
}