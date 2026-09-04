package datn_gym.service;

import datn_gym.dto.request.CreateScheduleRequest;
import datn_gym.dto.request.CompleteScheduleRequest;
import datn_gym.dto.request.NotificationCreateRequest;
import datn_gym.dto.request.UpdateScheduleRequest;
import datn_gym.dto.response.ScheduleSlotResponse;
import datn_gym.dto.response.ScheduleExerciseResponse;
import datn_gym.dto.response.TrainingStatsResponse;
import datn_gym.entity.Exercise;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.ScheduleExercise;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.ExerciseRepository;
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
    private final ExerciseRepository exerciseRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ================================================================
    // PT: Lấy lịch theo tuần
    // ================================================================
    public List<ScheduleSlotResponse> getWeekSchedules(String ptEmail, LocalDate weekStart) {
        User pt = userService.getUserByEmail(ptEmail);
        LocalDate weekEnd = weekStart.plusDays(6);
        return ptScheduleRepository
                .findByPtIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
                        pt.getId(), weekStart, weekEnd)
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
            weeks = request.getRecurringWeeks() != null ? request.getRecurringWeeks() : 8;
            if (weeks < 2 || weeks > 15) {
                throw new IllegalArgumentException("Lịch lặp phải kéo dài từ 2 đến tối đa 15 tuần");
            }
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
                    .status("SCHEDULED")
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
        if (!"SCHEDULED".equals(schedule.getStatus())) {
            throw new IllegalArgumentException("Chỉ buổi tập đang lên lịch mới có thể chỉnh sửa");
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
        if (!"SCHEDULED".equals(schedule.getStatus())) {
            throw new IllegalArgumentException("Buổi tập này đã được xử lý trước đó");
        }

        User member = schedule.getMember();
        String dateInfo;

        if (deleteAll && schedule.getRecurringGroupId() != null) {
            // Xóa tất cả buổi cùng nhóm lặp lại
            List<PtSchedule> group = ptScheduleRepository
                    .findByRecurringGroupIdAndStatus(schedule.getRecurringGroupId(), "SCHEDULED");
            dateInfo = group.size() + " buổi trong nhóm lặp lại";
            group.forEach(item -> item.setStatus("CANCELLED"));
            ptScheduleRepository.saveAll(group);
        } else {
            // Xóa đơn lẻ
            dateInfo = schedule.getScheduleDate().format(DATE_FMT);
            schedule.setStatus("CANCELLED");
            ptScheduleRepository.save(schedule);
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
                .findByMemberIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
                        member.getId(), weekStart, weekEnd)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private void validateTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu!");
        }
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (minutes < 30 || minutes > 120) {
            throw new IllegalArgumentException("Mỗi buổi tập phải kéo dài từ 30 phút đến tối đa 2 giờ");
        }
    }

    /**
     * Kiểm tra trùng giờ: nếu PT đã có lịch tại ngày date mà
     * khoảng [startTime, endTime) giao với khoảng [existing.start, existing.end)
     * thì ném lỗi.
     */
    private void checkOverlap(Integer ptId, LocalDate date, LocalTime start, LocalTime end, Integer excludeId) {
        List<PtSchedule> existing = ptScheduleRepository.findByPtIdAndScheduleDateAndStatus(ptId, date, "SCHEDULED");
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
                .actualNote(s.getActualNote())
                .completedAt(s.getCompletedAt())
                .exercises(s.getExercises().stream().map(item -> ScheduleExerciseResponse.builder()
                        .exerciseId(item.getExercise().getId())
                        .exerciseName(item.getExercise().getName()).muscleGroup(item.getExercise().getMuscleGroup())
                        .setCount(item.getSetCount()).repCount(item.getRepCount()).weightKg(item.getWeightKg())
                        .durationMinutes(item.getDurationMinutes()).note(item.getNote()).build()).toList())
                .build();
    }

    @Transactional
    public ScheduleSlotResponse completeSchedule(String ptEmail, Integer scheduleId,
                                                 CompleteScheduleRequest request) {
        User pt = userService.getUserByEmail(ptEmail);
        PtSchedule schedule = requireOwnedSchedule(pt, scheduleId);
        if (!"SCHEDULED".equals(schedule.getStatus())) {
            throw new IllegalArgumentException("Chỉ buổi tập đang lên lịch mới có thể hoàn thành");
        }
        if (java.time.LocalDateTime.of(schedule.getScheduleDate(), schedule.getEndTime())
                .isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Chỉ có thể hoàn thành sau khi buổi tập kết thúc");
        }
        schedule.getExercises().clear();
        request.getExercises().forEach(item -> {
            Exercise exercise = exerciseRepository.findById(item.getExerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài tập #" + item.getExerciseId()));
            schedule.getExercises().add(ScheduleExercise.builder().schedule(schedule).exercise(exercise)
                    .setCount(item.getSetCount()).repCount(item.getRepCount()).weightKg(item.getWeightKg())
                    .durationMinutes(item.getDurationMinutes()).note(item.getNote()).build());
        });
        schedule.setActualNote(request.getActualNote());
        schedule.setStatus("COMPLETED");
        schedule.setCompletedAt(java.time.LocalDateTime.now());
        return toResponse(ptScheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleSlotResponse markNoShow(String ptEmail, Integer scheduleId) {
        User pt = userService.getUserByEmail(ptEmail);
        PtSchedule schedule = requireOwnedSchedule(pt, scheduleId);
        if (!"SCHEDULED".equals(schedule.getStatus())) {
            throw new IllegalArgumentException("Chỉ buổi tập đang lên lịch mới có thể đánh dấu vắng");
        }
        if (java.time.LocalDateTime.of(schedule.getScheduleDate(), schedule.getStartTime())
                .isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Chỉ có thể đánh dấu vắng sau khi buổi tập bắt đầu");
        }
        schedule.setStatus("NO_SHOW");
        return toResponse(ptScheduleRepository.save(schedule));
    }

    public TrainingStatsResponse getTrainingStats(String ptEmail, Integer memberId,
                                                   LocalDate from, LocalDate to) {
        User pt = userService.getUserByEmail(ptEmail);
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học viên"));
        if (!membershipRepository.existsActiveMembershipByPtAndMember(pt.getId(), memberId)) {
            throw new IllegalArgumentException("Học viên không thuộc PT này");
        }
        if (to.isBefore(from) || java.time.temporal.ChronoUnit.DAYS.between(from, to) > 366) {
            throw new IllegalArgumentException("Khoảng thống kê không hợp lệ hoặc vượt quá 1 năm");
        }
        LocalDate effectiveTo = to.isAfter(LocalDate.now()) ? LocalDate.now() : to;
        if (effectiveTo.isBefore(from)) {
            throw new IllegalArgumentException("Khoảng thống kê chưa bắt đầu");
        }
        List<PtSchedule> sessions = ptScheduleRepository
                .findByPt_IdAndMember_IdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
                        pt.getId(), memberId, from, effectiveTo);
        java.util.Map<String, Long> exerciseFrequency = sessions.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus())).flatMap(s -> s.getExercises().stream())
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getExercise().getName(),
                        java.util.LinkedHashMap::new, java.util.stream.Collectors.counting()));
        java.util.Map<String, Long> muscleFrequency = sessions.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus())).flatMap(s -> s.getExercises().stream())
                .filter(e -> e.getExercise().getMuscleGroup() != null)
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getExercise().getMuscleGroup(),
                        java.util.LinkedHashMap::new, java.util.stream.Collectors.counting()));
        long completedMinutes = sessions.stream().filter(s -> "COMPLETED".equals(s.getStatus()))
                .mapToLong(s -> java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes()).sum();
        return TrainingStatsResponse.builder().memberId(memberId).memberName(member.getFullName())
                .fromDate(from.toString()).toDate(effectiveTo.toString()).scheduledSessions(count(sessions, "SCHEDULED"))
                .completedSessions(count(sessions, "COMPLETED")).cancelledSessions(count(sessions, "CANCELLED"))
                .noShowSessions(count(sessions, "NO_SHOW")).completedMinutes(completedMinutes)
                .muscleGroupFrequency(muscleFrequency).exerciseFrequency(exerciseFrequency)
                .sessions(sessions.stream().map(this::toResponse).toList()).build();
    }

    private long count(List<PtSchedule> schedules, String status) {
        return schedules.stream().filter(s -> status.equals(s.getStatus())).count();
    }

    private PtSchedule requireOwnedSchedule(User pt, Integer scheduleId) {
        PtSchedule schedule = ptScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập"));
        if (!schedule.getPt().getId().equals(pt.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xử lý buổi tập này");
        }
        return schedule;
    }

}
