package datn_gym.repository;

import datn_gym.entity.BaiViet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BaiVietRepository extends JpaRepository<BaiViet, Integer> {

    // DS bài viết đã PUBLISHED (trang public, phân trang)
    Page<BaiViet> findByTrangThaiOrderByNgayTaoDesc(String trangThai, Pageable pageable);

    // Bài viết của một tác giả (PT xem bài của mình)
    List<BaiViet> findByTacGia_IdOrderByNgayTaoDesc(Integer tacGiaId);

    // Bài viết của một tác giả theo trạng thái
    List<BaiViet> findByTacGia_IdAndTrangThaiOrderByNgayTaoDesc(
            Integer tacGiaId, String trangThai);

    // Tìm kiếm bài viết theo tiêu đề (trang tìm kiếm public)
    @Query("SELECT b FROM BaiViet b WHERE " +
           "b.trangThai = 'PUBLISHED' AND " +
           "LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.ngayTao DESC")
    List<BaiViet> timKiem(@Param("keyword") String tuKhoa);

    // Admin xem tất cả bài viết có phân trang + filter
    @Query("SELECT b FROM BaiViet b WHERE " +
           "(:trangThai IS NULL OR b.trangThai = :trangThai) AND " +
           "(:keyword IS NULL OR LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY b.ngayTao DESC")
    Page<BaiViet> findByAdmin(
            @Param("trangThai") String trangThai,
            @Param("keyword") String tuKhoa,
            Pageable pageable);

    // 5 bài viết mới nhất (trang chủ)
    @Query("SELECT b FROM BaiViet b WHERE b.trangThai = 'PUBLISHED' " +
           "ORDER BY b.ngayTao DESC")
    List<BaiViet> findTop5MoiNhat(Pageable pageable);
}
