package datn_gym.repository;

import datn_gym.entity.TrainingRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TrainingRouteRepository extends JpaRepository<TrainingRoute, Integer> {
    List<TrainingRoute> findByPt_Id(Integer ptId);
    List<TrainingRoute> findByPt_IdAndIsTemplateTrue(Integer ptId);
    List<TrainingRoute> findByPt_IdAndIsTemplateFalse(Integer ptId);
    List<TrainingRoute> findByMember_Id(Integer memberId);
    
    Optional<TrainingRoute> findByMember_IdAndStatus(Integer memberId, String status);
    List<TrainingRoute> findByPt_IdAndStatus(Integer ptId, String status);

    @Query("SELECT COUNT(t) FROM TrainingRoute t WHERE " +
           "t.pt.id = :ptId AND t.status = 'ASSIGNED'")
    Long countAssignedRoutes(@Param("ptId") Integer ptId);
}