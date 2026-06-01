package datn_gym.repository;

import datn_gym.entity.BaiTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BaiTapRepository extends JpaRepository<BaiTap, Integer> {

    // Thư viện bài tập của một PT
    List<BaiTap> findByNguoiTao_Id(Integer nguoiTaoId);

    // Lọc theo nhóm cơ
    List<BaiTap> findByNhomCo(String nhomCo);

    // Tìm kiếm theo tên bài tập
    @Query("SELECT b FROM BaiTap b WHERE " +
           "LOWER(b.ten) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BaiTap> timKiemTheoTen(@Param("keyword") String tuKhoa);

    // Tìm kiếm theo tên + nhóm cơ
    @Query("SELECT b FROM BaiTap b WHERE " +
           "(:keyword IS NULL OR LOWER(b.ten) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:nhomCo IS NULL OR b.nhomCo = :nhomCo)")
    List<BaiTap> timKiem(
            @Param("keyword") String tuKhoa,
            @Param("nhomCo") String nhomCo);

    // Danh sách nhóm cơ để hiển thị filter
    @Query("SELECT DISTINCT b.nhomCo FROM BaiTap b WHERE b.nhomCo IS NOT NULL ORDER BY b.nhomCo")
    List<String> findAllNhomCo();
}
