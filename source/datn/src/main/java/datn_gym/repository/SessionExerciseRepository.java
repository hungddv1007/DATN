package datn_gym.repository;

import datn_gym.entity.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Integer> {
    List<SessionExercise> findBySession_Id(Integer sessionId);

    @Query("SELECT COUNT(s) FROM SessionExercise s WHERE s.exercise.id = :exerciseId")
    Long countExerciseUsage(@Param("exerciseId") Integer exerciseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionExercise s WHERE s.session.id = :sessionId")
    void deleteBySessionId(@Param("sessionId") Integer sessionId);
}