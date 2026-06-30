package datn_gym.repository;

import datn_gym.entity.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Integer> {
    List<PlanExercise> findByPlanIdOrderByWeekNumberAscDayOfWeekAsc(Integer planId);
    void deleteByPlanId(Integer planId);
}
