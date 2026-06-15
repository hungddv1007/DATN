package datn_gym.repository;

import datn_gym.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // Kiểm tra đã điểm danh buổi này chưa
    boolean existsByMember_IdAndSession_Id(Integer memberId, Integer sessionId);

    // FIX N+1: Load member + session + session.route trong 1 câu SQL
    @EntityGraph(attributePaths = {"member", "session", "session.route"})
    Optional<Attendance> findByMember_IdAndSession_Id(Integer memberId, Integer sessionId);

    // Lịch sử điểm danh của HV
    @EntityGraph(attributePaths = {"member", "session", "session.route"})
    List<Attendance> findByMember_IdOrderByCheckInTimeDesc(Integer memberId);

    // Điểm danh của HV trong một lộ trình cụ thể
    @EntityGraph(attributePaths = {"member", "session", "session.route"})
    @Query("SELECT a FROM Attendance a WHERE " +
           "a.member.id = :memberId AND a.session.route.id = :routeId " +
           "ORDER BY a.session.weekNum ASC, a.session.dayNum ASC")
    List<Attendance> findByMemberAndRoute(
            @Param("memberId") Integer memberId,
            @Param("routeId") Integer routeId);

    // Đếm số buổi có mặt trong lộ trình — dùng cho thống kê
    @Query("SELECT COUNT(a) FROM Attendance a WHERE " +
           "a.member.id = :memberId AND a.session.route.id = :routeId " +
           "AND a.status = true")
    Long countPresentSessions(
            @Param("memberId") Integer memberId,
            @Param("routeId") Integer routeId);

    // PT xem điểm danh của một buổi tập
    @EntityGraph(attributePaths = {"member", "session"})
    List<Attendance> findBySession_Id(Integer sessionId);

    // FIX IDOR: Check attendance có thuộc member này không — dùng khi hủy điểm danh
    Optional<Attendance> findByIdAndMember_Id(Integer id, Integer memberId);
}
