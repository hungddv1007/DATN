package datn_gym.repository;

import datn_gym.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Integer> {
    List<TrainingPlan> findByPtIdOrderByCreatedAtDesc(Integer ptId);
}
