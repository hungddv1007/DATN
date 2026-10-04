package datn_gym.repository;

import datn_gym.entity.SaleReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleReferralCodeRepository extends JpaRepository<SaleReferralCode, Integer> {
    Optional<SaleReferralCode> findByCodeIgnoreCase(String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM SaleReferralCode c WHERE LOWER(c.code) = LOWER(:code)")
    Optional<SaleReferralCode> findByCodeForUpdate(@Param("code") String code);
    boolean existsByCodeIgnoreCase(String code);
    long countBySalesProfile_IdAndIsActiveTrue(Integer salesProfileId);
    List<SaleReferralCode> findBySalesProfile_IdOrderByCreatedAtDesc(Integer salesProfileId);
    default boolean isUsable(SaleReferralCode code) {
        return Boolean.TRUE.equals(code.getIsActive())
                && (code.getExpiresAt() == null || code.getExpiresAt().isAfter(LocalDateTime.now()));
    }
}
