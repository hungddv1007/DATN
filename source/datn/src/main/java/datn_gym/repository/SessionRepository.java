package datn_gym.repository;

import datn_gym.entity.Session;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {

    // FIX N+1: Load route trong 1 câu SQL
    @EntityGraph(attributePaths = {"route"})
    List<Session> findByRoute_IdOrderByWeekNumAscDayNumAsc(Integer routeId);

    List<Session> findByRoute_IdAndWeekNum(Integer routeId, Integer weekNum);

    // Kiểm tra trùng tuần + ngày trong cùng lộ trình
    Optional<Session> findByRoute_IdAndWeekNumAndDayNum(
            Integer routeId, Integer weekNum, Integer dayNum);

    List<Session> findByRoute_IdAndIsRestDayFalse(Integer routeId);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.route.id = :routeId AND s.isRestDay = false")
    Long countTrainingSessions(@Param("routeId") Integer routeId);

    @Query("SELECT MAX(s.weekNum) FROM Session s WHERE s.route.id = :routeId")
    Integer getMaxWeekNum(@Param("routeId") Integer routeId);

    // FIX IDOR: Check session có thuộc lộ trình này không
    Optional<Session> findByIdAndRoute_Id(Integer id, Integer routeId);

    // Dùng khi clone — lấy tất cả session của lộ trình gốc
    List<Session> findByRoute_Id(Integer routeId);
}
