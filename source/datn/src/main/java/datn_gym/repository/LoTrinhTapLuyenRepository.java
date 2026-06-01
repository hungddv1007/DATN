package datn_gym.repository;

import datn_gym.entity.LoTrinhTapLuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LoTrinhTapLuyenRepository extends JpaRepository<LoTrinhTapLuyen, Integer> {

    // Lộ trình của một PT (tất cả)
    List<LoTrinhTapLuyen> findByPt_Id(Integer ptId);

    // Template của một PT
    List<LoTrinhTapLuyen> findByPt_IdAndLaTemplateTrue(Integer ptId);

    // Lộ trình đã giao cho hội viên (không phải template)
    List<LoTrinhTapLuyen> findByPt_IdAndLaTemplateFalse(Integer ptId);

    // Lộ trình được giao cho một hội viên cụ thể
    List<LoTrinhTapLuyen> findByHoiVien_Id(Integer hoiVienId);

    // Lộ trình ASSIGNED đang chạy của một hội viên
    Optional<LoTrinhTapLuyen> findByHoiVien_IdAndTrangThai(
            Integer hoiVienId, String trangThai);

    // Lộ trình của PT theo trạng thái
    List<LoTrinhTapLuyen> findByPt_IdAndTrangThai(Integer ptId, String trangThai);

    // Đếm số lộ trình đang ASSIGNED của một PT
    @Query("SELECT COUNT(l) FROM LoTrinhTapLuyen l WHERE " +
           "l.pt.id = :ptId AND l.trangThai = 'ASSIGNED'")
    Long demLoTrinhDangChay(@Param("ptId") Integer ptId);
}
