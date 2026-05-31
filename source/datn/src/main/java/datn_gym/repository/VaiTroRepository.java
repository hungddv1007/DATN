package datn_gym.repository;

import datn_gym.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
    Optional<VaiTro> findByTen(String ten);
}
