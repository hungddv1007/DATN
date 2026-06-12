package datn_gym.repository;

import datn_gym.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    Optional<Membership> findByUser_IdAndStatus(Integer userId, String status);
    List<Membership> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Membership> findByPt_IdAndStatus(Integer ptId, String status);
    List<Membership> findByPt_Id(Integer ptId);

    @Query("SELECT m FROM Membership m WHERE " +
           "m.pt IS NULL AND m.status = 'ACTIVE' " +
           "AND m.gymPackage.hasPt = true")
    List<Membership> findUnassignedPtMemberships();

    @Query("SELECT COUNT(m) FROM Membership m WHERE " +
           "m.status = 'ACTIVE' AND " +
           "MONTH(m.createdAt) = :month AND YEAR(m.createdAt) = :year")
    Long countActiveMembershipsInMonth(
            @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT m FROM Membership m WHERE " +
           "m.status = 'ACTIVE' AND " +
           "m.endDate BETWEEN :fromDate AND :toDate")
    List<Membership> findExpiringMemberships(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    Page<Membership> findByStatus(String status, Pageable pageable);

    int countByPt_IdAndStatus(Integer ptId, String status);

    // Dùng cho: PtNote, PtComment, TrainingRoute, Review
    @Query("SELECT COUNT(m) > 0 FROM Membership m WHERE " +
           "m.pt.id = :ptId AND m.user.id = :memberId AND m.status = 'ACTIVE'")
    boolean existsActiveMembershipByPtAndMember(
            @Param("ptId") Integer ptId,
            @Param("memberId") Integer memberId);

    // FIX KIẾN TRÚC: Chuyển từ DietRepository về đây
    // Dùng cho: DietService.validateMemberIsVip()
    // Kiểm tra HV có gói ACTIVE với hasMealPlan = true không
    @Query("SELECT COUNT(m) > 0 FROM Membership m WHERE " +
           "m.user.id = :memberId AND m.status = 'ACTIVE' " +
           "AND m.gymPackage.hasMealPlan = true")
    boolean existsVipMembership(@Param("memberId") Integer memberId);

    // FIX KIẾN TRÚC: Chuyển từ DietRepository về đây
    // Dùng cho: DietService.validatePtCanManageDiet()
    // Kiểm tra HV có gói VIP ACTIVE và đang thuộc PT này không
    @Query("SELECT COUNT(m) > 0 FROM Membership m WHERE " +
           "m.pt.id = :ptId AND m.user.id = :memberId " +
           "AND m.status = 'ACTIVE' AND m.gymPackage.hasMealPlan = true")
    boolean existsVipMembershipByPtAndMember(
            @Param("ptId") Integer ptId,
            @Param("memberId") Integer memberId);
}
