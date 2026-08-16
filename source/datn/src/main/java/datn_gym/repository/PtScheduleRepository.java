package datn_gym.repository;

import datn_gym.entity.PtSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PtScheduleRepository extends JpaRepository<PtSchedule, Integer> {

    // PT lấy lịch theo khoảng ngày (theo tuần)
    List<PtSchedule> findByPtIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
            Integer ptId, LocalDate startDate, LocalDate endDate, String status);

    List<PtSchedule> findByPtIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
            Integer ptId, LocalDate startDate, LocalDate endDate);

    // PT lấy lịch trong 1 ngày (dùng cho overlap check)
    List<PtSchedule> findByPtIdAndScheduleDateAndStatus(
            Integer ptId, LocalDate date, String status);

    // Lấy tất cả buổi cùng nhóm lặp lại
    List<PtSchedule> findByRecurringGroupIdAndStatus(
            String recurringGroupId, String status);

    // Lấy tất cả buổi cùng nhóm lặp lại (kể cả đã hủy)
    List<PtSchedule> findByRecurringGroupId(String recurringGroupId);

    // Member xem lịch theo khoảng ngày
    List<PtSchedule> findByMemberIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
            Integer memberId, LocalDate startDate, LocalDate endDate, String status);

    List<PtSchedule> findByMemberIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
            Integer memberId, LocalDate startDate, LocalDate endDate);

    List<PtSchedule> findByPt_IdAndMember_IdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
            Integer ptId, Integer memberId, LocalDate startDate, LocalDate endDate);

    // Đếm số member ACTIVE (distinct) mà PT đang kèm
    @Query("SELECT COUNT(DISTINCT s.member.id) FROM PtSchedule s " +
           "WHERE s.pt.id = :ptId AND s.status = 'SCHEDULED'")
    long countDistinctActiveMembersByPtId(@Param("ptId") Integer ptId);

    // Kiểm tra member đã có PT nào kèm chưa (đang ACTIVE)
    @Query("SELECT COUNT(s) > 0 FROM PtSchedule s " +
           "WHERE s.member.id = :memberId AND s.status = 'SCHEDULED'")
    boolean existsActiveMemberSchedule(@Param("memberId") Integer memberId);
}
