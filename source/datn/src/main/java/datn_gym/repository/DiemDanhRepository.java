package datn_gym.repository;

import datn_gym.entity.DiemDanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DiemDanhRepository extends JpaRepository<DiemDanh, Integer> {

    // Kiểm tra hội viên đã điểm danh buổi này chưa
    Optional<DiemDanh> findByHoiVien_IdAndBuoiTap_Id(Integer hoiVienId, Integer buoiTapId);

    boolean existsByHoiVien_IdAndBuoiTap_Id(Integer hoiVienId, Integer buoiTapId);

    // Lịch sử điểm danh của một hội viên
    List<DiemDanh> findByHoiVien_IdOrderByThoiGianCheckInDesc(Integer hoiVienId);

    // Điểm danh của hội viên trong một lộ trình
    @Query("SELECT d FROM DiemDanh d WHERE " +
           "d.hoiVien.id = :hoiVienId AND d.buoiTap.loTrinh.id = :loTrinhId " +
           "ORDER BY d.thoiGianCheckIn ASC")
    List<DiemDanh> findByHoiVienVaLoTrinh(
            @Param("hoiVienId") Integer hoiVienId,
            @Param("loTrinhId") Integer loTrinhId);

    // Đếm số buổi có mặt của hội viên trong lộ trình
    @Query("SELECT COUNT(d) FROM DiemDanh d WHERE " +
           "d.hoiVien.id = :hoiVienId AND d.buoiTap.loTrinh.id = :loTrinhId " +
           "AND d.coMat = true")
    Long demSoBuoiCoMat(
            @Param("hoiVienId") Integer hoiVienId,
            @Param("loTrinhId") Integer loTrinhId);

    // DS điểm danh trong một buổi tập (PT xem)
    List<DiemDanh> findByBuoiTap_Id(Integer buoiTapId);
}
