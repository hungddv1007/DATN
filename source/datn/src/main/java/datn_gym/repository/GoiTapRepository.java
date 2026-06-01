package datn_gym.repository;

import datn_gym.entity.GoiTap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoiTapRepository extends JpaRepository<GoiTap, Integer> {

    Optional<GoiTap> findByTen(String ten);

    // Lấy các gói có PT kèm (Premium, VIP)
    java.util.List<GoiTap> findByCoPTTrue();

    // Lấy các gói có khẩu phần ăn (VIP)
    java.util.List<GoiTap> findByCoKhauPhanAnTrue();
}
