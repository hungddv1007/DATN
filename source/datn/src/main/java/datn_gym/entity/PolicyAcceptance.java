package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_acceptances")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PolicyAcceptance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersion policyVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(name = "acceptance_context", nullable = false, length = 30)
    private String acceptanceContext;

    @Column(name = "accepted_at", nullable = false)
    private LocalDateTime acceptedAt;

    @Column(name = "accepted_ip", length = 64)
    private String acceptedIp;

    @Column(name = "accepted_user_agent", length = 500)
    private String acceptedUserAgent;

    @PrePersist
    void onCreate() {
        if (acceptedAt == null) acceptedAt = LocalDateTime.now();
    }
}
