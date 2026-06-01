package datn_gym.repository;

import datn_gym.entity.GiaoDich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface GiaoDichRepository extends JpaRepository<GiaoDich, Integer> {

    // Lịch sử giao dịch của một membership
    List<GiaoDich> findByHoiVienGoi_Id(Integer hoiVienGoiId);

    // DS giao dịch đang PENDING (Admin xác nhận)
    List<GiaoDich> findByTrangThai(String trangThai);

    // DS giao dịch theo trạng thái có phân trang
    Page<GiaoDich> findByTrangThai(String trangThai, Pageable pageable);

    // Tất cả giao dịch có phân trang (Admin)
    Page<GiaoDich> findAllByOrderByNgayTaoDesc(Pageable pageable);

    // Doanh thu theo tháng/năm (Admin báo cáo)
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g WHERE " +
           "g.trangThai = 'CONFIRMED' AND " +
           "MONTH(g.ngayTao) = :thang AND YEAR(g.ngayTao) = :nam")
    BigDecimal tinhDoanhThuThang(
            @Param("thang") int thang,
            @Param("nam") int nam);

    // Doanh thu theo quý
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g WHERE " +
           "g.trangThai = 'CONFIRMED' AND " +
           "YEAR(g.ngayTao) = :nam AND " +
           "MONTH(g.ngayTao) BETWEEN :thangDau AND :thangCuoi")
    BigDecimal tinhDoanhThuQuy(
            @Param("nam") int nam,
            @Param("thangDau") int thangDau,
            @Param("thangCuoi") int thangCuoi);

    // Doanh thu theo năm
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g WHERE " +
           "g.trangThai = 'CONFIRMED' AND YEAR(g.ngayTao) = :nam")
    BigDecimal tinhDoanhThuNam(@Param("nam") int nam);

    // Doanh thu từng tháng trong năm (cho biểu đồ)
    @Query("SELECT MONTH(g.ngayTao), COALESCE(SUM(g.soTien), 0) " +
           "FROM GiaoDich g WHERE g.trangThai = 'CONFIRMED' AND YEAR(g.ngayTao) = :nam " +
           "GROUP BY MONTH(g.ngayTao) ORDER BY MONTH(g.ngayTao)")
    List<Object[]> thongKeDoanhThuTheoThang(@Param("nam") int nam);
}
