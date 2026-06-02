package datn_gym.repository;

import datn_gym.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByMember_IdAndSession_Id(Integer memberId, Integer sessionId);
    boolean existsByMember_IdAndSession_Id(Integer memberId, Integer sessionId);
    List<Attendance> findByMember_IdOrderByCheckInTimeDesc(Integer memberId);

    @Query("SELECT a FROM Attendance a WHERE " +
           "a.member.id = :memberId AND a.session.route.id = :routeId " +
           "ORDER BY a.checkInTime ASC")
    List<Attendance> findByMemberAndRoute(
            @Param("memberId") Integer memberId,
            @Param("routeId") Integer routeId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE " +
           "a.member.id = :memberId AND a.session.route.id = :routeId " +
           "AND a.status = true")
    Long countPresentSessions(
            @Param("memberId") Integer memberId,
            @Param("routeId") Integer routeId);

    List<Attendance> findBySession_Id(Integer sessionId);
}