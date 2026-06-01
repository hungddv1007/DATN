package datn_gym.repository;

import datn_gym.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Integer> {
    Optional<Diet> findByMember_IdAndDate(Integer memberId, LocalDate date);
    
    List<Diet> findByMember_IdAndDateBetweenOrderByDateAsc(
            Integer memberId, LocalDate fromDate, LocalDate toDate);
            
    List<Diet> findByMember_IdOrderByDateDesc(Integer memberId);
    List<Diet> findByPt_IdOrderByDateDesc(Integer ptId);
    List<Diet> findByPt_IdAndMember_IdOrderByDateDesc(Integer ptId, Integer memberId);
    boolean existsByMember_IdAndDate(Integer memberId, LocalDate date);

    @Query("SELECT COUNT(d) FROM Diet d WHERE " +
           "d.member.id = :memberId AND " +
           "MONTH(d.date) = :month AND YEAR(d.date) = :year")
    Long countDietsInMonth(
            @Param("memberId") Integer memberId,
            @Param("month") int month,
            @Param("year") int year);
}