package datn_gym.repository;

import datn_gym.entity.HoSoPT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface HoSoPTRepository extends JpaRepository<HoSoPT, Integer> {

    Optional<HoSoPT> findByNguoiDung_Id(Integer nguoiDungId);

    boolean existsByNguoiDung_Id(Integer nguoiDungId);

    // Lấy DS PT sắp xếp theo điểm đánh giá giảm dần (trang public)
    @Query("SELECT p FROM HoSoPT p ORDER BY p.diemDanhGia DESC")
    List<HoSoPT> findAllOrderByDiemDanhGiaDesc();

    // Tìm PT theo chuyên môn
    @Query("SELECT p FROM HoSoPT p WHERE " +
           "LOWER(p.chuyenMon) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<HoSoPT> timKiemTheoChuyenMon(@Param("keyword") String tuKhoa);
}
