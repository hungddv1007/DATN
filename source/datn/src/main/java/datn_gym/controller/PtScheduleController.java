package datn_gym.controller;

import datn_gym.dto.request.CreateScheduleRequest;
import datn_gym.dto.request.UpdateScheduleRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.ScheduleSlotResponse;
import datn_gym.service.PtScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PtScheduleController {

    private final PtScheduleService ptScheduleService;

    // ================================================================
    // PT Endpoints
    // ================================================================

    // GET /api/pt/schedules?weekStart=2026-07-21 — Lấy lịch PT theo tuần
    @GetMapping("/api/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<ScheduleSlotResponse>> getPtSchedules(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(ptScheduleService.getWeekSchedules(auth.getName(), weekStart));
    }

    // POST /api/pt/schedules — Tạo buổi tập mới (hỗ trợ recurring)
    @PostMapping("/api/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<ScheduleSlotResponse>> createSchedule(
            Authentication auth,
            @Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(ptScheduleService.createSchedule(auth.getName(), request));
    }

    // PUT /api/pt/schedules/{id} — Sửa 1 buổi tập
    @PutMapping("/api/pt/schedules/{id}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<ScheduleSlotResponse> updateSchedule(
            Authentication auth,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        return ResponseEntity.ok(ptScheduleService.updateSchedule(auth.getName(), id, request));
    }

    // DELETE /api/pt/schedules/{id}?deleteAll=false&notify=false — Xóa buổi tập
    @DeleteMapping("/api/pt/schedules/{id}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteSchedule(
            Authentication auth,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean deleteAll,
            @RequestParam(defaultValue = "false") boolean notify) {
        ptScheduleService.deleteSchedule(auth.getName(), id, deleteAll, notify);
        return ResponseEntity.ok(new MessageResponse("Đã xóa lịch tập thành công"));
    }

    // ================================================================
    // MEMBER Endpoints
    // ================================================================

    // GET /api/member/schedule?weekStart=2026-07-21 — Học viên xem lịch theo tuần
    @GetMapping("/api/member/schedule")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ScheduleSlotResponse>> getMemberSchedule(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(ptScheduleService.getMemberWeekSchedules(auth.getName(), weekStart));
    }
}
