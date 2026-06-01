package datn_gym.repository;

import datn_gym.entity.HoiVienGoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoiVienGoiRepository extends JpaRepository<HoiVienGoi, Integer> {

    // Lấy gói đang ACTIVE của hội viên
    Optional<HoiVienGoi> findByHoiVien_IdAndTrangThai(Integer hoiVienId, String trangThai);

    // Toàn bộ lịch sử gói của một hội viên
    List<HoiVienGoi> findByHoiVien_IdOrderByNgayTaoDesc(Integer hoiVienId);

    // DS hội viên được giao cho một PT (ACTIVE)
    List<HoiVienGoi> findByPt_IdAndTrangThai(Integer ptId, String trangThai);

    // DS hội viên được giao cho một PT (tất cả trạng thái)
    List<HoiVienGoi> findByPt_Id(Integer ptId);

    // DS hội viên chưa được gán PT (Premium đang ACTIVE)
    @Query("SELECT hv FROM HoiVienGoi hv WHERE " +
           "hv.pt IS NULL AND hv.trangThai = 'ACTIVE' " +
           "AND hv.goiTap.coPT = true")
    List<HoiVienGoi> findChuaDuocGanPT();

    // Đếm số hội viên ACTIVE trong tháng (Admin dashboard)
    @Query("SELECT COUNT(hv) FROM HoiVienGoi hv WHERE " +
           "hv.trangThai = 'ACTIVE' AND " +
           "MONTH(hv.ngayTao) = :thang AND YEAR(hv.ngayTao) = :nam")
    Long demHoiVienActiveTrongThang(
            @Param("thang") int thang,
            @Param("nam") int nam);

    // Lấy các membership sắp hết hạn trong N ngày tới (gửi thông báo)
    @Query("SELECT hv FROM HoiVienGoi hv WHERE " +
           "hv.trangThai = 'ACTIVE' AND " +
           "hv.ngayKetThuc BETWEEN :tuNgay AND :denNgay")
    List<HoiVienGoi> findSapHetHan(
            @Param("tuNgay") LocalDate tuNgay,
            @Param("denNgay") LocalDate denNgay);

    // Phân trang DS membership cho Admin
    Page<HoiVienGoi> findByTrangThai(String trangThai, Pageable pageable);
}
