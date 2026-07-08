package datn_gym.repository;

import datn_gym.entity.PackageDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageDiscountRepository extends JpaRepository<PackageDiscount, Integer> {

    // Lấy tất cả chiết khấu sắp xếp theo min_days tăng dần
    List<PackageDiscount> findAllByOrderByMinDaysAsc();

    // Tìm chiết khấu cao nhất phù hợp cho 1 gói + số ngày
    // Logic: lấy mức discount_percent lớn nhất trong các mốc mà:
    //   - package_id = :pkgId HOẶC package_id IS NULL (áp dụng tất cả)
    //   - min_days <= :days
    @Query("SELECT MAX(d.discountPercent) FROM PackageDiscount d " +
           "WHERE (d.gymPackage.id = :pkgId OR d.gymPackage IS NULL) " +
           "AND d.minDays <= :days")
    Optional<Integer> findBestDiscount(@Param("pkgId") Integer packageId, @Param("days") Integer days);
}
