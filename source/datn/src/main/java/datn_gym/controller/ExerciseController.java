package datn_gym.controller;

import datn_gym.dto.request.ExerciseRequest;
import datn_gym.dto.response.ExerciseResponse;
import datn_gym.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    // GET /api/exercises — Bất kỳ ai đăng nhập đều có thể xem danh sách bài tập
    @GetMapping("/api/exercises")
    public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    // === ADMIN CRUD ===

    @PostMapping("/api/admin/exercises")
    public ResponseEntity<ExerciseResponse> createExercise(
            Authentication authentication,
            @Valid @RequestBody ExerciseRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(exerciseService.createExercise(email, request));
    }

    @PutMapping("/api/admin/exercises/{id}")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Integer id,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, request));
    }

    @DeleteMapping("/api/admin/exercises/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
