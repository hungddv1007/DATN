package datn_gym.service;

import datn_gym.dto.request.CreateScheduleRequest;
import datn_gym.dto.request.NotificationCreateRequest;
import datn_gym.dto.request.UpdateScheduleRequest;
import datn_gym.dto.response.ScheduleSlotResponse;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ================================================================
    // PT: Lấy lịch theo tuần
    // ================================================================
    public List<ScheduleSlotResponse> getWeekSchedules(String ptEmail, LocalDate weekStart) {
        User pt = userService.getUserByEmail(ptEmail);
        LocalDate weekEnd = weekStart.plusDays(6);
        return ptScheduleRepository
                .findByPtIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
                        pt.getId(), weekStart, weekEnd, "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ================================================================
    // PT: Tạo lịch mới (hỗ trợ recurring + notification)
    // ================================================================
    @Transactional
    public List<ScheduleSlotResponse> createSchedule(String ptEmail, CreateScheduleRequest request) {
        User pt = userService.getUserByEmail(ptEmail);
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học viên"));

        // Kiểm tra học viên có thuộc PT này không
        if (!membershipRepository.existsActiveMembershipByPtAndMember(pt.getId(), member.getId())) {
            throw new IllegalArgumentException("Học viên này không thuộc quản lý của bạn hoặc gói tập đã hết hạn!");
        }

        // Validate giờ
        validateTime(request.getStartTime(), request.getEndTime());

        // Tính số tuần lặp lại
        int weeks = 1;
        String recurringGroupId = null;
        if (request.isRecurring()) {
            weeks = Math.min(52, Math.max(2, request.getRecurringWeeks() != null ? request.getRecurringWeeks() : 8));
            recurringGroupId = UUID.randomUUID().toString();
        }

        // Kiểm tra overlap cho TẤT CẢ các ngày trước khi tạo
        for (int w = 0; w < weeks; w++) {
            LocalDate date = request.getScheduleDate().plusWeeks(w);
            checkOverlap(pt.getId(), date, request.getStartTime(), request.getEndTime(), null);
        }

        // Tạo các buổi tập
        List<PtSchedule> created = new ArrayList<>();
        for (int w = 0; w < weeks; w++) {
            LocalDate date = request.getScheduleDate().plusWeeks(w);
            PtSchedule schedule = PtSchedule.builder()
                    .pt(pt)
                    .member(member)
                    .scheduleDate(date)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .exerciseNote(request.getExerciseNote())
                    .recurringGroupId(recurringGroupId)
                    .status("ACTIVE")
                    .build();
            created.add(ptScheduleRepository.save(schedule));
        }

        // Gửi thông báo nếu được chọn
        if (request.isSendNotification()) {
            String dateInfo = request.isRecurring()
                    ? String.format("%d buổi bắt đầu từ %s", weeks, request.getScheduleDate().format(DATE_FMT))
                    : request.getScheduleDate().format(DATE_FMT);
            sendScheduleNotification(ptEmail, member,
                    "Lịch tập mới",
                    String.format("PT %s đã xếp lịch tập cho bạn vào %s từ %s đến %s. Nội dung: %s",
                            pt.getFullName(), dateInfo,
                            request.getStartTime().format(TIME_FMT),
                            request.getEndTime().format(TIME_FMT),
                            request.getExerciseNote() != null ? request.getExerciseNote() : "Chưa ghi chú"));
        }

        return created.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ================================================================
    // PT: Sửa lịch
    // ================================================================
    @Transactional
    public ScheduleSlotResponse updateSchedule(String ptEmail, Integer scheduleId, UpdateScheduleRequest request) {
        User pt = userService.getUserByEmail(ptEmail);
        PtSchedule schedule = ptScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập"));

        // Kiểm tra quyền: chỉ PT sở hữu mới được sửa
        if (!schedule.getPt().getId().equals(pt.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa buổi tập này");
        }

        // Validate giờ
        validateTime(request.getStartTime(), request.getEndTime());

        // Kiểm tra overlap (trừ chính nó)
        checkOverlap(pt.getId(), request.getScheduleDate(), request.getStartTime(), request.getEndTime(), scheduleId);

        // Cập nhật
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setExerciseNote(request.getExerciseNote());

        PtSchedule updated = ptScheduleRepository.save(schedule);

        // Gửi thông báo nếu được chọn
        if (request.isSendNotification()) {
            sendScheduleNotification(ptEmail, schedule.getMember(),
                    "Cập nhật lịch tập",
                    String.format("PT %s đã cập nhật lịch tập của bạn vào %s từ %s đến %s.",
                            pt.getFullName(),
                            request.getScheduleDate().format(DATE_FMT),
                            request.getStartTime().format(TIME_FMT),
                            request.getEndTime().format(TIME_FMT)));
        }

        return toResponse(updated);
    }

    // ================================================================
    // PT: Xóa lịch (đơn lẻ hoặc cả nhóm lặp lại)
    // ================================================================
    @Transactional
    public void deleteSchedule(String ptEmail, Integer scheduleId, boolean deleteAll, boolean notify) {
        User pt = userService.getUserByEmail(ptEmail);
        PtSchedule schedule = ptScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập"));

        if (!schedule.getPt().getId().equals(pt.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa buổi tập này");
        }

        User member = schedule.getMember();
        String dateInfo;

        if (deleteAll && schedule.getRecurringGroupId() != null) {
            // Xóa tất cả buổi cùng nhóm lặp lại
            List<PtSchedule> group = ptScheduleRepository
                    .findByRecurringGroupIdAndStatus(schedule.getRecurringGroupId(), "ACTIVE");
            dateInfo = group.size() + " buổi trong nhóm lặp lại";
            ptScheduleRepository.deleteAll(group);
        } else {
            // Xóa đơn lẻ
            dateInfo = schedule.getScheduleDate().format(DATE_FMT);
            ptScheduleRepository.delete(schedule);
        }

        // Gửi thông báo
        if (notify) {
            sendScheduleNotification(ptEmail, member,
                    "Hủy lịch tập",
                    String.format("PT %s đã hủy lịch tập của bạn (%s).", pt.getFullName(), dateInfo));
        }
    }

    // ================================================================
    // MEMBER: Xem lịch theo tuần
    // ================================================================
    public List<ScheduleSlotResponse> getMemberWeekSchedules(String memberEmail, LocalDate weekStart) {
        User member = userService.getUserByEmail(memberEmail);
        LocalDate weekEnd = weekStart.plusDays(6);
        return ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
                        member.getId(), weekStart, weekEnd, "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private void validateTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu!");
        }
    }

    /**
     * Kiểm tra trùng giờ: nếu PT đã có lịch tại ngày date mà
     * khoảng [startTime, endTime) giao với khoảng [existing.start, existing.end)
     * thì ném lỗi.
     */
    private void checkOverlap(Integer ptId, LocalDate date, LocalTime start, LocalTime end, Integer excludeId) {
        List<PtSchedule> existing = ptScheduleRepository.findByPtIdAndScheduleDateAndStatus(ptId, date, "ACTIVE");
        for (PtSchedule s : existing) {
            if (excludeId != null && s.getId().equals(excludeId)) continue;
            // Overlap: start < existingEnd AND end > existingStart
            if (start.isBefore(s.getEndTime()) && end.isAfter(s.getStartTime())) {
                throw new IllegalArgumentException(
                        String.format("Trùng lịch vào ngày %s với %s (%s - %s)!",
                                date.format(DATE_FMT),
                                s.getMember().getFullName(),
                                s.getStartTime().format(TIME_FMT),
                                s.getEndTime().format(TIME_FMT)));
            }
        }
    }

    private void sendScheduleNotification(String senderEmail, User receiver, String title, String message) {
        try {
            NotificationCreateRequest notifReq = new NotificationCreateRequest();
            notifReq.setUserId(receiver.getId());
            notifReq.setTitle(title);
            notifReq.setMessage(message);
            notificationService.sendNotification(senderEmail, notifReq);
        } catch (Exception e) {
            // Không để lỗi thông báo ảnh hưởng flow chính
            System.err.println("Lỗi gửi thông báo: " + e.getMessage());
        }
    }

    private ScheduleSlotResponse toResponse(PtSchedule s) {
        return ScheduleSlotResponse.builder()
                .id(s.getId())
                .ptId(s.getPt().getId())
                .ptName(s.getPt().getFullName())
                .memberId(s.getMember().getId())
                .memberName(s.getMember().getFullName())
                .scheduleDate(s.getScheduleDate().format(DATE_FMT))
                .startTime(s.getStartTime().format(TIME_FMT))
                .endTime(s.getEndTime().format(TIME_FMT))
                .exerciseNote(s.getExerciseNote())
                .status(s.getStatus())
                .recurringGroupId(s.getRecurringGroupId())
                .build();
    }

}
