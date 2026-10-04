package datn_gym.repository;

import datn_gym.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByCreatedBy_Id(Integer createdById);
    List<Exercise> findByMuscleGroup(String muscleGroup);

    @Query("SELECT e FROM Exercise e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Exercise> searchByName(@Param("keyword") String keyword);

    @Query("SELECT e FROM Exercise e WHERE " +
           "(:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:muscleGroup IS NULL OR e.muscleGroup = :muscleGroup)")
    List<Exercise> search(
            @Param("keyword") String keyword,
            @Param("muscleGroup") String muscleGroup);

    @Query("SELECT DISTINCT e.muscleGroup FROM Exercise e WHERE e.muscleGroup IS NOT NULL ORDER BY e.muscleGroup")
    List<String> findAllMuscleGroups();
}