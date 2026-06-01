package datn_gym.repository;

import datn_gym.entity.BaiTapTrongBuoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface BaiTapTrongBuoiRepository extends JpaRepository<BaiTapTrongBuoi, Integer> {

    // Tất cả bài tập trong một buổi
    List<BaiTapTrongBuoi> findByBuoiTap_Id(Integer buoiTapId);

    // Bài tập cụ thể được dùng bao nhiêu lần (thống kê)
    @Query("SELECT COUNT(b) FROM BaiTapTrongBuoi b WHERE b.baiTap.id = :baiTapId")
    Long demSoLanDung(@Param("baiTapId") Integer baiTapId);

    // Xóa toàn bộ bài tập trong một buổi (khi PT cập nhật lại)
    @Modifying
    @Transactional
    @Query("DELETE FROM BaiTapTrongBuoi b WHERE b.buoiTap.id = :buoiTapId")
    void xoaTheoBuoiTap(@Param("buoiTapId") Integer buoiTapId);
}
