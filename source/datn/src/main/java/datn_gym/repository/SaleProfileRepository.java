package datn_gym.repository;

import datn_gym.entity.SaleProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleProfileRepository extends JpaRepository<SaleProfile, Integer> {
    Optional<SaleProfile> findByUser_Email(String email);
    Optional<SaleProfile> findByUser_Id(Integer userId);
    List<SaleProfile> findByIsOnlineTrueAndUser_StatusTrueOrderByIdAsc();
}
