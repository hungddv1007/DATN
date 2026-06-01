package datn_gym.repository;

import datn_gym.entity.ThongBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {

    // DS thông báo của người dùng (phân trang, mới nhất trước)
    Page<ThongBao> findByNguoiNhan_IdOrderByNgayTaoDesc(
            Integer nguoiNhanId, Pageable pageable);

    // DS thông báo chưa đọc
    List<ThongBao> findByNguoiNhan_IdAndDaDocFalseOrderByNgayTaoDesc(Integer nguoiNhanId);

    // Đếm số thông báo chưa đọc (hiển thị badge)
    long countByNguoiNhan_IdAndDaDocFalse(Integer nguoiNhanId);

    // Đánh dấu một thông báo đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.id = :id AND t.nguoiNhan.id = :nguoiNhanId")
    int danhDauDaDoc(
            @Param("id") Integer id,
            @Param("nguoiNhanId") Integer nguoiNhanId);

    // Đánh dấu TẤT CẢ thông báo của user đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.nguoiNhan.id = :nguoiNhanId AND t.daDoc = false")
    int danhDauTatCaDaDoc(@Param("nguoiNhanId") Integer nguoiNhanId);
}
