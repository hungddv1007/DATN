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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_code_id")
    private SaleReferralCode saleCode;

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

    @Column(name = "accepted_terms", nullable = false)
    @Builder.Default
    private Boolean acceptedTerms = false;

    @Column(name = "terms_accepted_at")
    private LocalDateTime termsAcceptedAt;

    @Column(name = "terms_version")
    private Integer termsVersion;

    @Column(name = "accepted_ip", length = 64)
    private String acceptedIp;

    @Column(name = "accepted_user_agent", length = 500)
    private String acceptedUserAgent;

    @Column(name = "customer_discount_percent", nullable = false)
    @Builder.Default
    private Integer customerDiscountPercent = 0;

    @Column(name = "gateway_order_id", length = 200)
    private String gatewayOrderId;

    @Column(name = "gateway_request_id", length = 50)
    private String gatewayRequestId;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Column(name = "gateway_pay_url", length = 1000)
    private String gatewayPayUrl;

    @Column(name = "gateway_deeplink", length = 1000)
    private String gatewayDeeplink;

    @Column(name = "gateway_qr_content", length = 2000)
    private String gatewayQrContent;

    @Column(name = "gateway_result_code")
    private Integer gatewayResultCode;

    @Column(name = "gateway_message", length = 500)
    private String gatewayMessage;

    @Column(name = "payment_expires_at")
    private LocalDateTime paymentExpiresAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
    }
}
