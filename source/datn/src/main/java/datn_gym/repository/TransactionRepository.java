package datn_gym.repository;

import datn_gym.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
       Optional<Transaction> findTopByMembership_IdOrderByCreatedAtDescIdDesc(Integer membershipId);

       List<Transaction> findByMembership_User_IdOrderByCreatedAtDescIdDesc(Integer userId);

       boolean existsByMembership_IdAndStatus(Integer membershipId, String status);

       List<Transaction> findByStatus(String status);

       List<Transaction> findByStatusAndCreatedAtBefore(
                     String status,
                     LocalDateTime createdBefore);

       Page<Transaction> findByStatus(String status, Pageable pageable);

       Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

       @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
                     "t.status = 'CONFIRMED' AND " +
                     "MONTH(t.createdAt) = :month AND YEAR(t.createdAt) = :year")
       BigDecimal calculateMonthlyRevenue(
                     @Param("month") int month,
                     @Param("year") int year);

       @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
                     "t.status = 'CONFIRMED' AND " +
                     "YEAR(t.createdAt) = :year AND " +
                     "MONTH(t.createdAt) BETWEEN :startMonth AND :endMonth")
       BigDecimal calculateQuarterlyRevenue(
                     @Param("year") int year,
                     @Param("startMonth") int startMonth,
                     @Param("endMonth") int endMonth);

       @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
                     "t.status = 'CONFIRMED' AND YEAR(t.createdAt) = :year")
       BigDecimal calculateYearlyRevenue(@Param("year") int year);

       @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.createdAt >= :startDate AND t.status = 'CONFIRMED'")
       BigDecimal calculateRevenueSince(@Param("startDate") LocalDateTime startDate);

       @Query("SELECT MONTH(t.createdAt), COALESCE(SUM(t.amount), 0) " +
                     "FROM Transaction t WHERE t.status = 'CONFIRMED' AND YEAR(t.createdAt) = :year " +
                     "GROUP BY MONTH(t.createdAt) ORDER BY MONTH(t.createdAt)")
       List<Object[]> getRevenueStatsByMonth(@Param("year") int year);
}
