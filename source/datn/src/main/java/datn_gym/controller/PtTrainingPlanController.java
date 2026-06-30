package datn_gym.controller;

import datn_gym.dto.request.TrainingPlanRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.TrainingPlanResponse;
import datn_gym.service.TrainingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pt/plans")
@RequiredArgsConstructor
public class PtTrainingPlanController {

    private final TrainingPlanService planService;

    // GET /api/pt/plans — Lấy tất cả lộ trình
    @GetMapping
    public ResponseEntity<List<TrainingPlanResponse>> getAll(Authentication auth) {
        return ResponseEntity.ok(planService.getAllByPt(auth.getName()));
    }

    // GET /api/pt/plans/{id} — Chi tiết lộ trình
    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanResponse> getDetail(@PathVariable Integer id, Authentication auth) {
        return ResponseEntity.ok(planService.getDetail(id, auth.getName()));
    }

    // POST /api/pt/plans — Tạo mới
    @PostMapping
    public ResponseEntity<TrainingPlanResponse> create(@RequestBody TrainingPlanRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(request, auth.getName()));
    }

    // PUT /api/pt/plans/{id} — Sửa
    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlanResponse> update(@PathVariable Integer id, @RequestBody TrainingPlanRequest request, Authentication auth) {
        return ResponseEntity.ok(planService.update(id, request, auth.getName()));
    }

    // DELETE /api/pt/plans/{id} — Xoá
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id, Authentication auth) {
        planService.delete(id, auth.getName());
        return ResponseEntity.ok(new MessageResponse("Đã xoá lộ trình thành công!"));
    }

    // POST /api/pt/plans/{id}/clone — Nhân bản
    @PostMapping("/{id}/clone")
    public ResponseEntity<TrainingPlanResponse> clone(@PathVariable Integer id, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.clone(id, auth.getName()));
    }
}
