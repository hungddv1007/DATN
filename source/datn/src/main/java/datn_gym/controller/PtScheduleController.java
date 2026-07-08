package datn_gym.controller;

import datn_gym.dto.request.PtScheduleRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.PtScheduleResponse;
import datn_gym.service.PtScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PtScheduleController {

    private final PtScheduleService scheduleService;

    // ==========================================
    // API CHO PT
    // ==========================================

    @GetMapping("/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<PtScheduleResponse>> getPtSchedules(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scheduleService.getSchedulesByPt(userDetails.getUsername()));
    }

    @GetMapping("/pt/schedules/member/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<PtScheduleResponse>> getPtSchedulesForMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByPtAndMember(userDetails.getUsername(), memberId));
    }

    @PostMapping("/pt/schedules")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtScheduleResponse> createSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PtScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createSchedule(userDetails.getUsername(), request));
    }

    @DeleteMapping("/pt/schedules/{id}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id) {
        scheduleService.deleteSchedule(userDetails.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Đã xóa slot kèm thành công."));
    }

    // ==========================================
    // API CHO MEMBER
    // ==========================================

    @GetMapping("/member/schedules")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<PtScheduleResponse>> getMemberSchedules(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scheduleService.getMySchedules(userDetails.getUsername()));
    }
}
