package datn_gym.repository;

import datn_gym.entity.DanhGiaPT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DanhGiaPTRepository extends JpaRepository<DanhGiaPT, Integer> {

    // Tất cả đánh giá của một PT (trang hồ sơ PT public)
    List<DanhGiaPT> findByPt_IdOrderByNgayTaoDesc(Integer ptId);

    // Kiểm tra hội viên đã đánh giá PT này chưa
    Optional<DanhGiaPT> findByHoiVien_IdAndPt_Id(Integer hoiVienId, Integer ptId);

    boolean existsByHoiVien_IdAndPt_Id(Integer hoiVienId, Integer ptId);

    // Tính điểm trung bình của PT
    @Query("SELECT AVG(CAST(d.soSao AS double)) FROM DanhGiaPT d WHERE d.pt.id = :ptId")
    Double tinhDiemTrungBinh(@Param("ptId") Integer ptId);

    // Đếm số đánh giá theo từng mức sao (1→5) của PT
    @Query("SELECT d.soSao, COUNT(d) FROM DanhGiaPT d WHERE d.pt.id = :ptId GROUP BY d.soSao")
    List<Object[]> thongKeSao(@Param("ptId") Integer ptId);

    // Đánh giá của hội viên
    List<DanhGiaPT> findByHoiVien_IdOrderByNgayTaoDesc(Integer hoiVienId);
}
