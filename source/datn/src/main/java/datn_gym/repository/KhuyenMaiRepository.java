package datn_gym.repository;

import datn_gym.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    Optional<KhuyenMai> findByMaCode(String maCode);

    boolean existsByMaCode(String maCode);

    List<KhuyenMai> findByDangHoatDongTrue();

    // Kiểm tra mã KM còn hiệu lực và còn lượt dùng
    // Áp dụng cho gói cụ thể HOẶC mã áp dụng toàn bộ (package_id IS NULL)
    @Query("SELECT k FROM KhuyenMai k WHERE k.maCode = :maCode " +
           "AND k.dangHoatDong = true " +
           "AND k.ngayBatDau <= :ngayHienTai " +
           "AND k.ngayKetThuc >= :ngayHienTai " +
           "AND (k.soLanToiDa IS NULL OR k.soLanDaDung < k.soLanToiDa) " +
           "AND (k.goiTap IS NULL OR k.goiTap.id = :goiTapId)")
    Optional<KhuyenMai> findMaHopLe(
            @Param("maCode") String maCode,
            @Param("ngayHienTai") LocalDate ngayHienTai,
            @Param("goiTapId") Integer goiTapId);

    // Lấy tất cả KM còn hiệu lực (Admin dashboard)
    @Query("SELECT k FROM KhuyenMai k WHERE " +
           "k.dangHoatDong = true AND k.ngayKetThuc >= :ngayHienTai")
    List<KhuyenMai> findChuaHetHan(@Param("ngayHienTai") LocalDate ngayHienTai);
}
