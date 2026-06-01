package datn_gym.repository;

import datn_gym.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByMembership_Id(Integer membershipId);
    List<Transaction> findByStatus(String status);
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

    @Query("SELECT MONTH(t.createdAt), COALESCE(SUM(t.amount), 0) " +
           "FROM Transaction t WHERE t.status = 'CONFIRMED' AND YEAR(t.createdAt) = :year " +
           "GROUP BY MONTH(t.createdAt) ORDER BY MONTH(t.createdAt)")
    List<Object[]> getRevenueStatsByMonth(@Param("year") int year);
}