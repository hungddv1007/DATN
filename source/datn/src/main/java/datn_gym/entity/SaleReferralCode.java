package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_referral_codes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleReferralCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_profile_id", nullable = false)
    private SaleProfile salesProfile;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 255)
    private String description;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "one_time_per_member", nullable = false)
    @Builder.Default
    private Boolean oneTimePerMember = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
