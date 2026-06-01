package datn_gym.repository;

import datn_gym.entity.KhauPhanAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KhauPhanAnRepository extends JpaRepository<KhauPhanAn, Integer> {

    // Khẩu phần ăn của hội viên trong ngày cụ thể
    Optional<KhauPhanAn> findByHoiVien_IdAndNgay(Integer hoiVienId, LocalDate ngay);

    // Khẩu phần ăn của hội viên trong khoảng ngày (xem lịch theo tuần)
    List<KhauPhanAn> findByHoiVien_IdAndNgayBetweenOrderByNgayAsc(
            Integer hoiVienId, LocalDate tuNgay, LocalDate denNgay);

    // Tất cả khẩu phần của hội viên (sắp xếp mới nhất)
    List<KhauPhanAn> findByHoiVien_IdOrderByNgayDesc(Integer hoiVienId);

    // DS khẩu phần PT đã lên cho các hội viên
    List<KhauPhanAn> findByPt_IdOrderByNgayDesc(Integer ptId);

    // PT xem khẩu phần của một hội viên cụ thể
    List<KhauPhanAn> findByPt_IdAndHoiVien_IdOrderByNgayDesc(
            Integer ptId, Integer hoiVienId);

    // Kiểm tra PT đã lên khẩu phần cho ngày này chưa
    boolean existsByHoiVien_IdAndNgay(Integer hoiVienId, LocalDate ngay);

    // Đếm số ngày có thực đơn trong tháng
    @Query("SELECT COUNT(k) FROM KhauPhanAn k WHERE " +
           "k.hoiVien.id = :hoiVienId AND " +
           "MONTH(k.ngay) = :thang AND YEAR(k.ngay) = :nam")
    Long demSoNgayCo(
            @Param("hoiVienId") Integer hoiVienId,
            @Param("thang") int thang,
            @Param("nam") int nam);
}
