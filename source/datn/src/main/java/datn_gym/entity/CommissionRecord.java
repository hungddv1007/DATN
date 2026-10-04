package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommissionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_profile_id", nullable = false)
    private SaleProfile salesProfile;

    @Column(name = "base_amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal baseAmount;

    @Column(name = "commission_rate", nullable = false)
    private Integer commissionRate;

    @Column(name = "commission_amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal commissionAmount;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "payable_at")
    private LocalDateTime payableAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
