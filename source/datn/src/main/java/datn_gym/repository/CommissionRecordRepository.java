package datn_gym.repository;

import datn_gym.entity.CommissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommissionRecordRepository extends JpaRepository<CommissionRecord, Long> {
    Optional<CommissionRecord> findByTransaction_Id(Integer transactionId);
    List<CommissionRecord> findBySalesProfile_IdOrderByCreatedAtDesc(Integer profileId);
    List<CommissionRecord> findAllByOrderByCreatedAtDesc();
}
