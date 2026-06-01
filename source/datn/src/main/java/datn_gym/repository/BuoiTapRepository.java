package datn_gym.repository;

import datn_gym.entity.BuoiTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BuoiTapRepository extends JpaRepository<BuoiTap, Integer> {

    // Tất cả buổi tập của một lộ trình
    List<BuoiTap> findByLoTrinh_IdOrderByTuanAscNgayTrongTuanAsc(Integer loTrinhId);

    // Buổi tập theo tuần
    List<BuoiTap> findByLoTrinh_IdAndTuan(Integer loTrinhId, Integer tuan);

    // Buổi tập theo tuần + ngày cụ thể
    Optional<BuoiTap> findByLoTrinh_IdAndTuanAndNgayTrongTuan(
            Integer loTrinhId, Integer tuan, Integer ngayTrongTuan);

    // Chỉ lấy buổi tập thực (không phải rest day)
    List<BuoiTap> findByLoTrinh_IdAndLaNgayNghiFalse(Integer loTrinhId);

    // Đếm tổng số buổi tập (không tính rest day) của một lộ trình
    @Query("SELECT COUNT(b) FROM BuoiTap b WHERE " +
           "b.loTrinh.id = :loTrinhId AND b.laNgayNghi = false")
    Long demSoBuoiTap(@Param("loTrinhId") Integer loTrinhId);

    // Lấy số tuần tối đa của lộ trình
    @Query("SELECT MAX(b.tuan) FROM BuoiTap b WHERE b.loTrinh.id = :loTrinhId")
    Integer layTuanToiDa(@Param("loTrinhId") Integer loTrinhId);
}
