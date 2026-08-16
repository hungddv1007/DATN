package datn_gym.repository;

import datn_gym.entity.MembershipTransfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MembershipTransferRepository extends JpaRepository<MembershipTransfer, Long> {
    List<MembershipTransfer> findBySender_IdOrderByCreatedAtDesc(Integer senderId);
    List<MembershipTransfer> findByRecipient_IdOrderByCreatedAtDesc(Integer recipientId);
    boolean existsBySourceMembership_IdAndStatus(Integer membershipId, String status);
    long countBySender_IdAndStatus(Integer senderId, String status);
    List<MembershipTransfer> findByStatusAndExpiresAtBefore(String status, LocalDateTime cutoff);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM MembershipTransfer t WHERE t.id = :id")
    Optional<MembershipTransfer> findByIdForUpdate(@Param("id") Long id);
}
