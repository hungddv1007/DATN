package datn_gym.repository;

import datn_gym.entity.GhiChuPT;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GhiChuPTRepository extends JpaRepository<GhiChuPT, Integer> {

    // Ghi chú của PT về một hội viên (PT xem)
    List<GhiChuPT> findByPt_IdAndHoiVien_IdOrderByNgayTaoDesc(
            Integer ptId, Integer hoiVienId);

    // Tất cả ghi chú của một PT
    List<GhiChuPT> findByPt_IdOrderByNgayTaoDesc(Integer ptId);
}
