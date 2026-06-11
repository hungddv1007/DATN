package datn_gym.controller;

import datn_gym.dto.request.SessionCreateRequest;
import datn_gym.dto.request.SessionExerciseRequest;
import datn_gym.dto.request.TrainingRouteCreateRequest;
import datn_gym.dto.request.TrainingRouteUpdateRequest;
import datn_gym.dto.response.*;
import datn_gym.service.TrainingRouteService;
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
@RequiredArgsConstructor
public class TrainingRouteController {

    private final TrainingRouteService trainingRouteService;

    // ================================================================
    // PT APIs — Quản lý lộ trình
    // ================================================================

    // GET /api/pt/training-routes
    @GetMapping("/api/pt/training-routes")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<TrainingRouteSummaryResponse>> getAllMyRoutes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                trainingRouteService.getAllMyRoutes(userDetails.getUsername()));
    }

    // GET /api/pt/training-routes/{routeId}
    @GetMapping("/api/pt/training-routes/{routeId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteResponse> getRouteDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                trainingRouteService.getRouteDetail(
                        userDetails.getUsername(), routeId));
    }

    // POST /api/pt/training-routes
    @PostMapping("/api/pt/training-routes")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteSummaryResponse> createRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TrainingRouteCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRouteService.createRoute(
                        userDetails.getUsername(), request));
    }

    // PUT /api/pt/training-routes/{routeId}
    @PutMapping("/api/pt/training-routes/{routeId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteSummaryResponse> updateRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @Valid @RequestBody TrainingRouteUpdateRequest request) {
        return ResponseEntity.ok(
                trainingRouteService.updateRoute(
                        userDetails.getUsername(), routeId, request));
    }

    // DELETE /api/pt/training-routes/{routeId}
    @DeleteMapping("/api/pt/training-routes/{routeId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        trainingRouteService.deleteRoute(userDetails.getUsername(), routeId);
        return ResponseEntity.ok(new MessageResponse("Xóa lộ trình thành công"));
    }

    // POST /api/pt/training-routes/{routeId}/assign/{memberId}
    @PostMapping("/api/pt/training-routes/{routeId}/assign/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteSummaryResponse> assignRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(
                trainingRouteService.assignRoute(
                        userDetails.getUsername(), routeId, memberId));
    }

    // PUT /api/pt/training-routes/{routeId}/complete
    @PutMapping("/api/pt/training-routes/{routeId}/complete")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteSummaryResponse> completeRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                trainingRouteService.completeRoute(
                        userDetails.getUsername(), routeId));
    }

    // POST /api/pt/training-routes/{routeId}/clone
    @PostMapping("/api/pt/training-routes/{routeId}/clone")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<TrainingRouteSummaryResponse> cloneRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRouteService.cloneRoute(
                        userDetails.getUsername(), routeId));
    }

    // ================================================================
    // PT APIs — Quản lý buổi tập
    // ================================================================

    // POST /api/pt/training-routes/{routeId}/sessions
    @PostMapping("/api/pt/training-routes/{routeId}/sessions")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<SessionResponse> addSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @Valid @RequestBody SessionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRouteService.addSession(
                        userDetails.getUsername(), routeId, request));
    }

    // PUT /api/pt/training-routes/{routeId}/sessions/{sessionId}
    @PutMapping("/api/pt/training-routes/{routeId}/sessions/{sessionId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<SessionResponse> updateSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer sessionId,
            @Valid @RequestBody SessionCreateRequest request) {
        return ResponseEntity.ok(
                trainingRouteService.updateSession(
                        userDetails.getUsername(), routeId, sessionId, request));
    }

    // DELETE /api/pt/training-routes/{routeId}/sessions/{sessionId}
    @DeleteMapping("/api/pt/training-routes/{routeId}/sessions/{sessionId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer sessionId) {
        trainingRouteService.deleteSession(
                userDetails.getUsername(), routeId, sessionId);
        return ResponseEntity.ok(new MessageResponse("Xóa buổi tập thành công"));
    }

    // ================================================================
    // PT APIs — Quản lý bài tập trong buổi
    // ================================================================

    // POST /api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises
    @PostMapping("/api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<SessionExerciseResponse> addExercise(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer sessionId,
            @Valid @RequestBody SessionExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRouteService.addExerciseToSession(
                        userDetails.getUsername(), routeId, sessionId, request));
    }

    // PUT /api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises/{seId}
    @PutMapping("/api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises/{seId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<SessionExerciseResponse> updateExercise(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer sessionId,
            @PathVariable Integer seId,
            @Valid @RequestBody SessionExerciseRequest request) {
        return ResponseEntity.ok(
                trainingRouteService.updateExerciseInSession(
                        userDetails.getUsername(), routeId, sessionId, seId, request));
    }

    // DELETE /api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises/{seId}
    @DeleteMapping("/api/pt/training-routes/{routeId}/sessions/{sessionId}/exercises/{seId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteExercise(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId,
            @PathVariable Integer sessionId,
            @PathVariable Integer seId) {
        trainingRouteService.deleteExerciseFromSession(
                userDetails.getUsername(), routeId, sessionId, seId);
        return ResponseEntity.ok(new MessageResponse("Xóa bài tập thành công"));
    }

    // ================================================================
    // MEMBER APIs — Xem lộ trình
    // ================================================================

    // GET /api/member/training-routes
    @GetMapping("/api/member/training-routes")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<TrainingRouteSummaryResponse>> getMemberRoutes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                trainingRouteService.getMemberRoutes(userDetails.getUsername()));
    }

    // GET /api/member/training-routes/{routeId}
    @GetMapping("/api/member/training-routes/{routeId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<TrainingRouteResponse> getMemberRouteDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                trainingRouteService.getMemberRouteDetail(
                        userDetails.getUsername(), routeId));
    }
}
