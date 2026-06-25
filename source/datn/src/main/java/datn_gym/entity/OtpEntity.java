package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;
}
