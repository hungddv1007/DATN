package datn_gym.controller;

import datn_gym.dto.request.ScheduleRequest;
import datn_gym.dto.response.ScheduleSlotResponse;
import datn_gym.service.PtScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PtScheduleController {

    private final PtScheduleService ptScheduleService;

    // ================================================================
    // PT Endpoints
    // ================================================================

    // GET /api/pt/schedules - Lấy toàn bộ lịch huấn luyện của PT đang đăng nhập
    @GetMapping("/api/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<ScheduleSlotResponse>> getPtSchedules(Authentication auth) {
        return ResponseEntity.ok(ptScheduleService.getAllByPt(auth.getName()));
    }

    // GET /api/pt/schedules/member/{memberId} - Lấy lịch huấn luyện với 1 học viên
    @GetMapping("/api/pt/schedules/member/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<ScheduleSlotResponse>> getPtMemberSchedule(
            Authentication auth,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(ptScheduleService.getByPtAndMember(auth.getName(), memberId));
    }

    // POST /api/pt/schedules - Lưu hoặc cập nhật thời khóa biểu của 1 học viên
    @PostMapping("/api/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<ScheduleSlotResponse>> saveMemberSchedule(
            Authentication auth,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(ptScheduleService.saveMemberSchedule(auth.getName(), request));
    }

    // ================================================================
    // MEMBER Endpoints
    // ================================================================

    // GET /api/member/schedule - Học viên xem lịch biểu kèm PT của mình
    @GetMapping("/api/member/schedule")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ScheduleSlotResponse>> getMemberSchedule(Authentication auth) {
        return ResponseEntity.ok(ptScheduleService.getMySchedule(auth.getName()));
    }
}
