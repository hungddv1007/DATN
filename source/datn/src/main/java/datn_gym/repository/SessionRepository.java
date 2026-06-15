package datn_gym.repository;

import datn_gym.entity.Session;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {

    // FIX LazyInitializationException: load route + route.pt trong 1 SQL
    // Dùng khi cần truy cập session.getRoute() hoặc session.getRoute().getPt()
    @EntityGraph(attributePaths = {"route", "route.pt", "route.member"})
    Optional<Session> findWithRouteById(Integer id);

    List<Session> findByRoute_IdOrderByWeekNumAscDayNumAsc(Integer routeId);
    List<Session> findByRoute_IdAndWeekNum(Integer routeId, Integer weekNum);
    Optional<Session> findByRoute_IdAndWeekNumAndDayNum(
            Integer routeId, Integer weekNum, Integer dayNum);
    List<Session> findByRoute_IdAndIsRestDayFalse(Integer routeId);

    @Query("SELECT COUNT(s) FROM Session s WHERE " +
           "s.route.id = :routeId AND s.isRestDay = false")
    Long countTrainingSessions(@Param("routeId") Integer routeId);

    @Query("SELECT MAX(s.weekNum) FROM Session s WHERE s.route.id = :routeId")
    Integer getMaxWeekNum(@Param("routeId") Integer routeId);

    Optional<Session> findByIdAndRoute_Id(Integer id, Integer routeId);
    List<Session> findByRoute_Id(Integer routeId);
}
