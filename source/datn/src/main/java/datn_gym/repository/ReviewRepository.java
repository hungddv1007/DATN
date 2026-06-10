package datn_gym.repository;

import datn_gym.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // FIX N+1: Load pt và member trong 1 câu SQL
    @EntityGraph(attributePaths = {"pt", "member"})
    List<Review> findByPt_IdOrderByCreatedAtDesc(Integer ptId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<Review> findByMember_IdOrderByCreatedAtDesc(Integer memberId);

    // Kiểm tra HV đã đánh giá PT này chưa — dùng trước khi tạo mới
    Optional<Review> findByMember_IdAndPt_Id(Integer memberId, Integer ptId);
    boolean existsByMember_IdAndPt_Id(Integer memberId, Integer ptId);

    // Tính điểm trung bình — gọi sau khi tạo/sửa/xóa review
    @Query("SELECT AVG(CAST(r.ratingStar AS double)) FROM Review r WHERE r.pt.id = :ptId")
    Double calculateAverageRating(@Param("ptId") Integer ptId);

    // Thống kê số lượng từng mức sao (1→5) — dùng cho trang hồ sơ PT public
    @Query("SELECT r.ratingStar, COUNT(r) FROM Review r WHERE r.pt.id = :ptId GROUP BY r.ratingStar ORDER BY r.ratingStar")
    List<Object[]> getRatingStats(@Param("ptId") Integer ptId);

    // Đếm tổng số review của PT — dùng trong PtProfileService.toResponse()
    int countByPt_Id(Integer ptId);
}