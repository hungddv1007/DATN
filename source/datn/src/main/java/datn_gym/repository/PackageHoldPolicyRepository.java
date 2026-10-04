package datn_gym.repository;

import datn_gym.entity.PackageHoldPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PackageHoldPolicyRepository extends JpaRepository<PackageHoldPolicy, Integer> {
    @Query("SELECT p FROM PackageHoldPolicy p WHERE p.gymPackage.id = :packageId " +
            "AND p.isActive = true AND p.minDurationDays <= :days " +
            "AND (p.maxDurationDays IS NULL OR p.maxDurationDays >= :days) " +
            "ORDER BY p.minDurationDays DESC")
    Optional<PackageHoldPolicy> findApplicable(@Param("packageId") Integer packageId,
                                               @Param("days") Integer days);
}
