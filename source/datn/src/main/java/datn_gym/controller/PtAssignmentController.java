package datn_gym.controller;

import datn_gym.dto.request.AssignPlanRequest;
import datn_gym.dto.response.PlanAssignmentResponse;
import datn_gym.service.PlanAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pt/assignments")
@RequiredArgsConstructor
public class PtAssignmentController {

    private final PlanAssignmentService assignmentService;

    // GET /api/pt/assignments — Lấy tất cả phân công
    @GetMapping
    public ResponseEntity<List<PlanAssignmentResponse>> getAll(Authentication auth) {
        return ResponseEntity.ok(assignmentService.getAllByPt(auth.getName()));
    }

    // POST /api/pt/assignments — Gán lộ trình cho nhiều member
    @PostMapping
    public ResponseEntity<List<PlanAssignmentResponse>> assign(@RequestBody AssignPlanRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assign(request, auth.getName()));
    }

    // PUT /api/pt/assignments/{id}/status — Thay đổi trạng thái
    @PutMapping("/{id}/status")
    public ResponseEntity<PlanAssignmentResponse> changeStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String newStatus = body.get("status");
        return ResponseEntity.ok(assignmentService.changeStatus(id, newStatus, auth.getName()));
    }
}
