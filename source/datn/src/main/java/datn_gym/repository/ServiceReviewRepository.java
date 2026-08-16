package datn_gym.repository;

import datn_gym.entity.ServiceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Integer> {
    List<ServiceReview> findByIsFeaturedTrueOrderByUpdatedAtDesc();
    @Query("SELECT r FROM ServiceReview r ORDER BY r.isFeatured DESC, r.updatedAt DESC")
    List<ServiceReview> findAllFeaturedFirst();
    List<ServiceReview> findByMember_IdOrderByCreatedAtDesc(Integer memberId);
    Optional<ServiceReview> findByIdAndMember_Id(Integer id, Integer memberId);
    boolean existsByTransaction_Id(Integer transactionId);
}
