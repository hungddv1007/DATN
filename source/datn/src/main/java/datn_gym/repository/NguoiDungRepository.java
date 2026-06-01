package datn_gym.repository;

import datn_gym.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByEmail(String email);

    // Lấy DS người dùng theo role
    List<NguoiDung> findByVaiTro_Name(String tenVaiTro);

    // Lấy DS người dùng theo role có phân trang
    Page<NguoiDung> findByVaiTro_Name(String tenVaiTro, Pageable pageable);

    // Lấy DS người dùng theo trạng thái
    List<NguoiDung> findByTrangThai(Boolean trangThai);

    // Tìm kiếm theo tên hoặc email (Admin)
    @Query("SELECT u FROM NguoiDung u WHERE " +
           "(:keyword IS NULL OR LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NguoiDung> timKiem(@Param("keyword") String tuKhoa, Pageable pageable);

    // Tìm kiếm theo keyword + lọc theo role (Admin)
    @Query("SELECT u FROM NguoiDung u WHERE " +
           "(:tenVaiTro IS NULL OR u.vaiTro.name = :tenVaiTro) AND " +
           "(:keyword IS NULL OR LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NguoiDung> timKiemTheoVaiTro(
            @Param("tenVaiTro") String tenVaiTro,
            @Param("keyword") String tuKhoa,
            Pageable pageable);

    // Lấy DS tất cả PT đang hoạt động (cho trang public)
    @Query("SELECT u FROM NguoiDung u WHERE u.vaiTro.name = 'PT' AND u.trangThai = true")
    List<NguoiDung> findAllPTDangHoatDong();
}
