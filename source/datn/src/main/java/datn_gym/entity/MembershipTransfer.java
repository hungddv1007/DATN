package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_transfers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MembershipTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_membership_id", nullable = false)
    private Membership sourceMembership;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING_RECIPIENT";

    @Column(name = "remaining_days_at_request", nullable = false)
    private Integer remainingDaysAtRequest;

    @Column(name = "remaining_days_at_accept")
    private Integer remainingDaysAtAccept;

    @Column(name = "deducted_days")
    private Integer deductedDays;

    @Column(name = "transferred_days")
    private Integer transferredDays;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
