package datn_gym.repository;

import datn_gym.entity.GymPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GymPackageRepository extends JpaRepository<GymPackage, Integer> {
    Optional<GymPackage> findByName(String name);
    List<GymPackage> findByHasPtTrue();
    List<GymPackage> findByHasMealPlanTrue();
}