package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "pt_id", nullable = false)
    private User pt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Column(name = "day_type", nullable = false, length = 20)
    private String dayType; // TRAINING_DAY, REST_DAY, SPECIFIC_DATE

    @Column(name = "diet_date")
    private LocalDate dietDate; // NULL cho mẫu TRAINING_DAY/REST_DAY

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "breakfast", columnDefinition = "NVARCHAR(MAX)")
    private String breakfast;

    @Column(name = "snack_morning", columnDefinition = "NVARCHAR(MAX)")
    private String snackMorning;

    @Column(name = "lunch", columnDefinition = "NVARCHAR(MAX)")
    private String lunch;

    @Column(name = "snack_afternoon", columnDefinition = "NVARCHAR(MAX)")
    private String snackAfternoon;

    @Column(name = "dinner", columnDefinition = "NVARCHAR(MAX)")
    private String dinner;

    @Column(name = "calories")
    @Builder.Default
    private Integer calories = 0;

    @Column(name = "protein_g")
    @Builder.Default
    private Integer proteinG = 0;

    @Column(name = "carbs_g")
    @Builder.Default
    private Integer carbsG = 0;

    @Column(name = "fat_g")
    @Builder.Default
    private Integer fatG = 0;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}