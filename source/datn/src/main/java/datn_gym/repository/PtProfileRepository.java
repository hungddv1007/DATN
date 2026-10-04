package datn_gym.repository;

import datn_gym.entity.PtProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PtProfileRepository extends JpaRepository<PtProfile, Integer> {
    Optional<PtProfile> findByUser_Id(Integer userId);
    boolean existsByUser_Id(Integer userId);

    @Query("SELECT p FROM PtProfile p ORDER BY p.ratingScore DESC")
    List<PtProfile> findAllOrderByRatingScoreDesc();

    @Query("SELECT p FROM PtProfile p WHERE " +
           "LOWER(p.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PtProfile> searchBySpecialization(@Param("keyword") String keyword);
}