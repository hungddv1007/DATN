package datn_gym.repository;

import datn_gym.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {
    Optional<OtpEntity> findTopByEmailOrderByExpirationTimeDesc(String email);
    
    long countByEmailAndCreatedAtAfter(String email, LocalDateTime createdAt);
}
