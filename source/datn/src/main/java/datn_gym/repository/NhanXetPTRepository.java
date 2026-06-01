package datn_gym.repository;

import datn_gym.entity.NhanXetPT;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NhanXetPTRepository extends JpaRepository<NhanXetPT, Integer> {

    // Nhận xét PT gửi cho hội viên (hội viên xem)
    List<NhanXetPT> findByHoiVien_IdOrderByNgayTaoDesc(Integer hoiVienId);

    // Nhận xét PT gửi cho hội viên trong một lộ trình cụ thể
    List<NhanXetPT> findByHoiVien_IdAndLoTrinh_IdOrderByNgayTaoDesc(
            Integer hoiVienId, Integer loTrinhId);

    // Tất cả nhận xét PT đã gửi
    List<NhanXetPT> findByPt_IdOrderByNgayTaoDesc(Integer ptId);

    // Nhận xét của PT cho hội viên (PT xem lại)
    List<NhanXetPT> findByPt_IdAndHoiVien_IdOrderByNgayTaoDesc(
            Integer ptId, Integer hoiVienId);
}
