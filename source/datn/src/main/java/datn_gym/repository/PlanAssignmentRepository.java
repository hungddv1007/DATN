package datn_gym.repository;

import datn_gym.entity.PlanAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanAssignmentRepository extends JpaRepository<PlanAssignment, Integer> {
    List<PlanAssignment> findByPtIdOrderByCreatedAtDesc(Integer ptId);
    List<PlanAssignment> findByPlanId(Integer planId);
    boolean existsByPlanIdAndMemberIdAndStatus(Integer planId, Integer memberId, String status);
    long countByPlanIdAndStatus(Integer planId, String status);
    long countByPlanId(Integer planId);
}
