package datn_gym.repository;

import datn_gym.entity.HoSoHoiVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HoSoHoiVienRepository extends JpaRepository<HoSoHoiVien, Integer> {

    Optional<HoSoHoiVien> findByNguoiDung_Id(Integer nguoiDungId);

    boolean existsByNguoiDung_Id(Integer nguoiDungId);
}
