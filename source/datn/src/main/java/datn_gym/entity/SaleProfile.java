package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "level_number", nullable = false)
    @Builder.Default
    private Integer levelNumber = 1;

    @Column(name = "successful_customers", nullable = false)
    @Builder.Default
    private Integer successfulCustomers = 0;

    @Column(name = "is_online", nullable = false)
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "max_concurrent_chats", nullable = false)
    @Builder.Default
    private Integer maxConcurrentChats = 3;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public int discountPercent() { return levelNumber == 3 ? 15 : levelNumber == 2 ? 10 : 5; }
    public int commissionRate() { return levelNumber == 3 ? 5 : levelNumber == 2 ? 4 : 3; }
}
