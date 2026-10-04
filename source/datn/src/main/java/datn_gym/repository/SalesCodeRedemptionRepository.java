package datn_gym.repository;

import datn_gym.entity.SalesCodeRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesCodeRedemptionRepository extends JpaRepository<SalesCodeRedemption, Long> {
    Optional<SalesCodeRedemption> findByTransaction_Id(Integer transactionId);
    boolean existsBySaleCode_IdAndMember_IdAndStatusIn(Integer codeId, Integer memberId, List<String> statuses);

    @Query("SELECT COUNT(DISTINCT r.member.id) FROM SalesCodeRedemption r " +
            "WHERE r.saleCode.salesProfile.id = :profileId AND r.status = 'CONFIRMED'")
    long countDistinctConfirmedMembers(@Param("profileId") Integer profileId);
}
