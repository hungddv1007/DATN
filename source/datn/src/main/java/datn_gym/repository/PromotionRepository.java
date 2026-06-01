package datn_gym.repository;

import datn_gym.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    Optional<Promotion> findByCode(String code);
    boolean existsByCode(String code);
    List<Promotion> findByIsActiveTrue();

    @Query("SELECT p FROM Promotion p WHERE p.code = :code " +
           "AND p.isActive = true " +
           "AND p.startDate <= :currentDate " +
           "AND p.endDate >= :currentDate " +
           "AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage) " +
           "AND (p.gymPackage IS NULL OR p.gymPackage.id = :packageId)")
    Optional<Promotion> findValidPromotion(
            @Param("code") String code,
            @Param("currentDate") LocalDate currentDate,
            @Param("packageId") Integer packageId);

    @Query("SELECT p FROM Promotion p WHERE " +
           "p.isActive = true AND p.endDate >= :currentDate")
    List<Promotion> findUnexpiredPromotions(@Param("currentDate") LocalDate currentDate);
}