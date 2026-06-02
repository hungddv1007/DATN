package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "diets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Diet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private User pt;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "breakfast", columnDefinition = "NVARCHAR(MAX)")
    private String breakfast;

    @Column(name = "lunch", columnDefinition = "NVARCHAR(MAX)")
    private String lunch;

    @Column(name = "dinner", columnDefinition = "NVARCHAR(MAX)")
    private String dinner;
}