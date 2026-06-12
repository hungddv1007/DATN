package datn_gym.repository;

import datn_gym.entity.Diet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Integer> {

    // FIX N+1: Load pt và member trong 1 câu SQL
    @EntityGraph(attributePaths = {"pt", "member"})
    Optional<Diet> findByMember_IdAndDate(Integer memberId, LocalDate date);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByMember_IdAndDateBetweenOrderByDateAsc(
            Integer memberId, LocalDate fromDate, LocalDate toDate);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByMember_IdOrderByDateDesc(Integer memberId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByPt_IdOrderByDateDesc(Integer ptId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByPt_IdAndMember_IdOrderByDateDesc(Integer ptId, Integer memberId);

    // Kiểm tra trùng ngày trước khi tạo mới
    boolean existsByMember_IdAndDate(Integer memberId, LocalDate date);

    // FIX IDOR: 1 câu SQL vừa tìm vừa check ownership khi update/delete
    Optional<Diet> findByIdAndPt_Id(Integer id, Integer ptId);

    // FIX Lỗi 3: PT chỉ xem thực đơn do chính mình tạo
    // Filter theo cả pt_id + member_id + date
    @EntityGraph(attributePaths = {"pt", "member"})
    Optional<Diet> findByPt_IdAndMember_IdAndDate(
            Integer ptId, Integer memberId, LocalDate date);

    // Thống kê số ngày có thực đơn trong tháng
    @Query("SELECT COUNT(d) FROM Diet d WHERE " +
           "d.member.id = :memberId AND " +
           "MONTH(d.date) = :month AND YEAR(d.date) = :year")
    Long countDietsInMonth(
            @Param("memberId") Integer memberId,
            @Param("month") int month,
            @Param("year") int year);
}
