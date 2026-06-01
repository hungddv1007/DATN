package datn_gym.repository;

import datn_gym.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByPt_IdOrderByCreatedAtDesc(Integer ptId);
    Optional<Review> findByMember_IdAndPt_Id(Integer memberId, Integer ptId);
    boolean existsByMember_IdAndPt_Id(Integer memberId, Integer ptId);

    @Query("SELECT AVG(CAST(r.ratingStar AS double)) FROM Review r WHERE r.pt.id = :ptId")
    Double calculateAverageRating(@Param("ptId") Integer ptId);

    @Query("SELECT r.ratingStar, COUNT(r) FROM Review r WHERE r.pt.id = :ptId GROUP BY r.ratingStar")
    List<Object[]> getRatingStats(@Param("ptId") Integer ptId);

    List<Review> findByMember_IdOrderByCreatedAtDesc(Integer memberId);
}