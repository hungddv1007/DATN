package datn_gym.repository;

import datn_gym.entity.PtSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PtScheduleRepository extends JpaRepository<PtSchedule, Integer> {

    // Tất cả lịch kèm ACTIVE của 1 PT
    List<PtSchedule> findByPtIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(
            Integer ptId, String status);

    // Lịch kèm với 1 member cụ thể
    List<PtSchedule> findByPtIdAndMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(
            Integer ptId, Integer memberId, String status);

    // Kiểm tra xung đột: slot này đã có ai chưa?
    Optional<PtSchedule> findByPtIdAndDayOfWeekAndSlotIndexAndStatus(
            Integer ptId, Integer dayOfWeek, Integer slotIndex, String status);

    // Đếm số member ACTIVE (distinct) mà PT đang kèm
    @Query("SELECT COUNT(DISTINCT s.member.id) FROM PtSchedule s " +
           "WHERE s.pt.id = :ptId AND s.status = 'ACTIVE'")
    long countDistinctActiveMembersByPtId(@Param("ptId") Integer ptId);

    // Kiểm tra member đã có PT nào kèm chưa (đang ACTIVE)
    @Query("SELECT COUNT(s) > 0 FROM PtSchedule s " +
           "WHERE s.member.id = :memberId AND s.status = 'ACTIVE'")
    boolean existsActiveMemberSchedule(@Param("memberId") Integer memberId);

    // Lịch kèm của member (member xem lịch)
    List<PtSchedule> findByMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(
            Integer memberId, String status);
}
