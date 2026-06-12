package datn_gym.repository;

import datn_gym.entity.TrainingRoute;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingRouteRepository extends JpaRepository<TrainingRoute, Integer> {

    // FIX N+1: Load pt và member trong 1 câu SQL
    @EntityGraph(attributePaths = {"pt", "member"})
    List<TrainingRoute> findByPt_Id(Integer ptId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<TrainingRoute> findByPt_IdAndIsTemplateTrue(Integer ptId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<TrainingRoute> findByPt_IdAndIsTemplateFalse(Integer ptId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<TrainingRoute> findByMember_Id(Integer memberId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<TrainingRoute> findByPt_IdAndStatus(Integer ptId, String status);

    // Lấy lộ trình ASSIGNED đang chạy của HV
    Optional<TrainingRoute> findByMember_IdAndStatus(Integer memberId, String status);

    // Đếm lộ trình đang ASSIGNED của PT
    @Query("SELECT COUNT(t) FROM TrainingRoute t WHERE t.pt.id = :ptId AND t.status = 'ASSIGNED'")
    Long countAssignedRoutes(@Param("ptId") Integer ptId);

    // FIX IDOR: 1 câu SQL vừa tìm vừa check ownership
    Optional<TrainingRoute> findByIdAndPt_Id(Integer id, Integer ptId);

    // Kiểm tra HV đã có lộ trình ASSIGNED chưa
    boolean existsByMember_IdAndStatus(Integer memberId, String status);
}
