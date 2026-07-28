package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    /**
     * Snapshot của thay đổi sẽ được áp dụng sau khi giao dịch được duyệt.
     * Không cập nhật Membership khi giao dịch vẫn còn PENDING.
     */
    @Column(name = "requested_duration_days")
    private Integer requestedDurationDays;

    /**
     * true với dữ liệu legacy đã sửa membership trước khi được duyệt.
     */
    @Column(name = "operation_applied", nullable = false)
    @Builder.Default
    private Boolean operationApplied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_package_id")
    private GymPackage requestedPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_pt_id")
    private User requestedPt;

    @Column(name = "amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal amount;

    @Column(name = "original_amount", precision = 12, scale = 0)
    private BigDecimal originalAmount;

    // CASH | BANK | ONLINE
    @Column(name = "payment_method", length = 20)
    private String paymentMethod; 

    // NEW | RENEW | UPGRADE
    @Column(name = "type", length = 20)
    @Builder.Default
    private String type = "NEW";

    // PENDING | CONFIRMED | CANCELLED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING"; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
    }
}
