package datn_gym.controller;

import datn_gym.dto.response.PlanAssignmentResponse;
import datn_gym.dto.response.TrainingPlanResponse;
import datn_gym.entity.PlanAssignment;
import datn_gym.entity.TrainingPlan;
import datn_gym.entity.User;
import datn_gym.repository.PlanAssignmentRepository;
import datn_gym.repository.PlanExerciseRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member/plans")
@RequiredArgsConstructor
public class MemberPlanController {

    private final PlanAssignmentRepository assignmentRepo;
    private final PlanExerciseRepository exerciseRepo;
    private final UserRepository userRepo;

    @GetMapping("/active")
    public ResponseEntity<TrainingPlanResponse> getActivePlan(Authentication auth) {
        User member = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));

        // Tìm assignment ACTIVE của member
        // Nếu có nhiều, tạm lấy cái mới nhất
        List<PlanAssignment> assignments = assignmentRepo.findAll().stream()
                .filter(a -> a.getMember().getId().equals(member.getId()) && "ACTIVE".equals(a.getStatus()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PlanAssignment activeAssignment = assignments.get(0);
        TrainingPlan plan = activeAssignment.getPlan();

        // Convert sang Response
        TrainingPlanResponse response = TrainingPlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .durationWeeks(plan.getDurationWeeks())
                .difficulty(plan.getDifficulty())
                .goal(plan.getGoal())
                .createdAt(plan.getCreatedAt())
                .build();

        // Lấy bài tập
        var exercises = exerciseRepo.findByPlanIdOrderByWeekNumberAscDayOfWeekAsc(plan.getId());
        var exResponses = exercises.stream().map(ex -> 
            datn_gym.dto.response.PlanExerciseResponse.builder()
                .id(ex.getId())
                .exerciseName(ex.getExerciseName())
                .sets(ex.getSets())
                .reps(ex.getReps())
                .restSeconds(ex.getRestSeconds())
                .dayOfWeek(ex.getDayOfWeek())
                .weekNumber(ex.getWeekNumber())
                .build()
        ).collect(Collectors.toList());

        response.setExercises(exResponses);

        return ResponseEntity.ok(response);
    }
}
