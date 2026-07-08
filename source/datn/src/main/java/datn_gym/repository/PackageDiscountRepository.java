package datn_gym.repository;

import datn_gym.entity.PackageDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageDiscountRepository extends JpaRepository<PackageDiscount, Integer> {

    // Tìm tất cả chiết khấu, sắp theo minDays tăng dần
    List<PackageDiscount> findAllByOrderByMinDaysAsc();

    // Tìm chiết khấu áp dụng cho gói cụ thể hoặc tất cả gói, với số ngày >= minDays
    // Lấy mức giảm cao nhất phù hợp
    @Query("SELECT MAX(d.discountPercent) FROM PackageDiscount d " +
           "WHERE (d.gymPackage.id = :packageId OR d.gymPackage IS NULL) " +
           "AND d.minDays <= :days")
    Optional<Integer> findBestDiscount(@Param("packageId") Integer packageId,
                                       @Param("days") int days);
}
