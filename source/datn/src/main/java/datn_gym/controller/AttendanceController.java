package datn_gym.controller;

import datn_gym.dto.response.AttendanceResponse;
import datn_gym.dto.response.AttendanceSummaryResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ================================================================
    // MEMBER APIs
    // ================================================================

    // POST /api/member/attendances/{sessionId}
    // Member điểm danh một buổi tập
    @PostMapping("/api/member/attendances/{sessionId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AttendanceResponse> checkIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer sessionId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceService.checkIn(
                        userDetails.getUsername(), sessionId));
    }

    // DELETE /api/member/attendances/{attendanceId}
    // Member hủy điểm danh
    @DeleteMapping("/api/member/attendances/{attendanceId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MessageResponse> cancelCheckIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer attendanceId) {
        attendanceService.cancelCheckIn(userDetails.getUsername(), attendanceId);
        return ResponseEntity.ok(new MessageResponse("Hủy điểm danh thành công"));
    }

    // GET /api/member/attendances/route/{routeId}
    // Member xem lịch sử điểm danh trong một lộ trình
    @GetMapping("/api/member/attendances/route/{routeId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendanceByRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                attendanceService.getMyAttendanceByRoute(
                        userDetails.getUsername(), routeId));
    }

    // GET /api/member/attendances/route/{routeId}/summary
    // Member xem thống kê điểm danh trong lộ trình
    @GetMapping("/api/member/attendances/route/{routeId}/summary")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AttendanceSummaryResponse> getMyAttendanceSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                attendanceService.getMyAttendanceSummary(
                        userDetails.getUsername(), routeId));
    }

    // ================================================================
    // PT APIs
    // ================================================================

    // GET /api/pt/attendances/member/{memberId}/route/{routeId}
    // PT xem điểm danh của HV trong một lộ trình
    @GetMapping("/api/pt/attendances/member/{memberId}/route/{routeId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<AttendanceResponse>> getMemberAttendanceByRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                attendanceService.getMemberAttendanceByRoute(
                        userDetails.getUsername(), memberId, routeId));
    }

    // GET /api/pt/attendances/member/{memberId}/route/{routeId}/summary
    // PT xem thống kê điểm danh của HV
    @GetMapping("/api/pt/attendances/member/{memberId}/route/{routeId}/summary")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<AttendanceSummaryResponse> getMemberAttendanceSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                attendanceService.getMemberAttendanceSummary(
                        userDetails.getUsername(), memberId, routeId));
    }

    // GET /api/pt/attendances/session/{sessionId}
    // PT xem điểm danh tất cả HV trong một buổi tập
    @GetMapping("/api/pt/attendances/session/{sessionId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceBySession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(
                attendanceService.getAttendanceBySession(
                        userDetails.getUsername(), sessionId));
    }
}
