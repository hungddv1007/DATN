package datn_gym.repository;

import datn_gym.entity.SessionExercise;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Integer> {

    // FIX N+1: Load exercise trong 1 câu SQL
    @EntityGraph(attributePaths = {"exercise", "exercise.createdBy"})
    List<SessionExercise> findBySession_Id(Integer sessionId);

    // FIX IDOR: Check session_exercise có thuộc session này không
    Optional<SessionExercise> findByIdAndSession_Id(Integer id, Integer sessionId);

    @Query("SELECT COUNT(s) FROM SessionExercise s WHERE s.exercise.id = :exerciseId")
    Long countExerciseUsage(@Param("exerciseId") Integer exerciseId);

    // Dùng khi xóa toàn bộ bài tập trong buổi (thay thế lại)
    @Modifying
    @Transactional
    @Query("DELETE FROM SessionExercise s WHERE s.session.id = :sessionId")
    void deleteBySessionId(@Param("sessionId") Integer sessionId);

    // FIX N+1 QUAN TRỌNG: Dùng khi clone lộ trình và xem chi tiết lộ trình
    @EntityGraph(attributePaths = {"exercise", "session"})
    List<SessionExercise> findBySession_IdIn(List<Integer> sessionIds);
}