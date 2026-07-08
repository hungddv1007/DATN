package datn_gym.repository;

import datn_gym.entity.PtSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PtScheduleRepository extends JpaRepository<PtSchedule, Integer> {

    List<PtSchedule> findByPt_IdAndStatus(Integer ptId, String status);

    List<PtSchedule> findByMember_IdAndStatus(Integer memberId, String status);

    List<PtSchedule> findByPt_IdAndMember_IdAndStatus(Integer ptId, Integer memberId, String status);

    Optional<PtSchedule> findByPt_IdAndDayOfWeekAndTimeSlotAndStatus(
            Integer ptId, Integer dayOfWeek, String timeSlot, String status);
            
    Optional<PtSchedule> findByMember_IdAndDayOfWeekAndTimeSlotAndStatus(
            Integer memberId, Integer dayOfWeek, String timeSlot, String status);
}
