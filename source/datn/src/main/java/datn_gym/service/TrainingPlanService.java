package datn_gym.service;

import datn_gym.dto.request.PlanExerciseRequest;
import datn_gym.dto.request.TrainingPlanRequest;
import datn_gym.dto.response.PlanExerciseResponse;
import datn_gym.dto.response.TrainingPlanResponse;
import datn_gym.entity.PlanExercise;
import datn_gym.entity.TrainingPlan;
import datn_gym.entity.User;
import datn_gym.repository.PlanAssignmentRepository;
import datn_gym.repository.PlanExerciseRepository;
import datn_gym.repository.TrainingPlanRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final TrainingPlanRepository planRepo;
    private final PlanExerciseRepository exerciseRepo;
    private final PlanAssignmentRepository assignmentRepo;
    private final UserRepository userRepo;

    // Lấy tất cả lộ trình của PT
    public List<TrainingPlanResponse> getAllByPt(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        List<TrainingPlan> plans = planRepo.findByPtIdOrderByCreatedAtDesc(pt.getId());
        return plans.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy chi tiết lộ trình (kèm exercises)
    public TrainingPlanResponse getDetail(Integer planId, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        TrainingPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lộ trình!"));
        if (!plan.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xem lộ trình này!");
        }
        return toDetailResponse(plan);
    }

    // Tạo lộ trình mới
    @Transactional
    public TrainingPlanResponse create(TrainingPlanRequest request, String ptEmail) {
        User pt = getUserByEmail(ptEmail);

        TrainingPlan plan = TrainingPlan.builder()
                .pt(pt)
                .title(request.getTitle())
                .description(request.getDescription())
                .durationWeeks(request.getDurationWeeks())
                .difficulty(request.getDifficulty())
                .goal(request.getGoal())
                .isTemplate(request.getIsTemplate() != null ? request.getIsTemplate() : false)
                .build();

        planRepo.save(plan);

        // Thêm bài tập
        if (request.getExercises() != null) {
            for (PlanExerciseRequest exReq : request.getExercises()) {
                PlanExercise exercise = PlanExercise.builder()
                        .plan(plan)
                        .exerciseName(exReq.getExerciseName())
                        .sets(exReq.getSets())
                        .reps(exReq.getReps())
                        .restSeconds(exReq.getRestSeconds())
                        .dayOfWeek(exReq.getDayOfWeek())
                        .weekNumber(exReq.getWeekNumber() != null ? exReq.getWeekNumber() : 1)
                        .build();
                exerciseRepo.save(exercise);
            }
        }

        return toResponse(plan);
    }

    // Sửa lộ trình
    @Transactional
    public TrainingPlanResponse update(Integer planId, TrainingPlanRequest request, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        TrainingPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lộ trình!"));
        if (!plan.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền sửa lộ trình này!");
        }

        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setDurationWeeks(request.getDurationWeeks());
        plan.setDifficulty(request.getDifficulty());
        plan.setGoal(request.getGoal());
        plan.setIsTemplate(request.getIsTemplate() != null ? request.getIsTemplate() : false);

        // Xoá exercises cũ, thêm mới
        plan.getExercises().clear();
        planRepo.save(plan); // flush xoá

        if (request.getExercises() != null) {
            for (PlanExerciseRequest exReq : request.getExercises()) {
                PlanExercise exercise = PlanExercise.builder()
                        .plan(plan)
                        .exerciseName(exReq.getExerciseName())
                        .sets(exReq.getSets())
                        .reps(exReq.getReps())
                        .restSeconds(exReq.getRestSeconds())
                        .dayOfWeek(exReq.getDayOfWeek())
                        .weekNumber(exReq.getWeekNumber() != null ? exReq.getWeekNumber() : 1)
                        .build();
                plan.getExercises().add(exercise);
            }
        }

        planRepo.save(plan);
        return toResponse(plan);
    }

    // Xoá lộ trình (cascade xoá exercises + assignments)
    @Transactional
    public void delete(Integer planId, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        TrainingPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lộ trình!"));
        if (!plan.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xoá lộ trình này!");
        }
        planRepo.delete(plan);
    }

    // Nhân bản lộ trình
    @Transactional
    public TrainingPlanResponse clone(Integer planId, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        TrainingPlan original = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lộ trình!"));
        if (!original.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền nhân bản lộ trình này!");
        }

        TrainingPlan cloned = TrainingPlan.builder()
                .pt(pt)
                .title("Copy — " + original.getTitle())
                .description(original.getDescription())
                .durationWeeks(original.getDurationWeeks())
                .difficulty(original.getDifficulty())
                .goal(original.getGoal())
                .isTemplate(false) // Bản sao không phải mẫu
                .exercises(new ArrayList<>())
                .assignments(new ArrayList<>())
                .build();

        planRepo.save(cloned);

        // Copy tất cả exercises
        List<PlanExercise> originalExercises = exerciseRepo.findByPlanIdOrderByWeekNumberAscDayOfWeekAsc(planId);
        for (PlanExercise ex : originalExercises) {
            PlanExercise clonedEx = PlanExercise.builder()
                    .plan(cloned)
                    .exerciseName(ex.getExerciseName())
                    .sets(ex.getSets())
                    .reps(ex.getReps())
                    .restSeconds(ex.getRestSeconds())
                    .dayOfWeek(ex.getDayOfWeek())
                    .weekNumber(ex.getWeekNumber())
                    .build();
            exerciseRepo.save(clonedEx);
        }

        return toResponse(cloned);
    }

    // === Helper methods ===

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
    }

    private TrainingPlanResponse toResponse(TrainingPlan plan) {
        long activeCount = assignmentRepo.countByPlanIdAndStatus(plan.getId(), "ACTIVE");
        long totalCount = assignmentRepo.countByPlanId(plan.getId());

        return TrainingPlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .durationWeeks(plan.getDurationWeeks())
                .difficulty(plan.getDifficulty())
                .goal(plan.getGoal())
                .isTemplate(plan.getIsTemplate())
                .createdAt(plan.getCreatedAt())
                .totalExercises(plan.getExercises() != null ? plan.getExercises().size() : 0)
                .activeAssignments(activeCount)
                .totalAssignments(totalCount)
                .build();
    }

    private TrainingPlanResponse toDetailResponse(TrainingPlan plan) {
        TrainingPlanResponse response = toResponse(plan);
        // Kèm danh sách exercises chi tiết
        List<PlanExercise> exercises = exerciseRepo.findByPlanIdOrderByWeekNumberAscDayOfWeekAsc(plan.getId());
        response.setExercises(exercises.stream().map(this::toExerciseResponse).collect(Collectors.toList()));
        return response;
    }

    private PlanExerciseResponse toExerciseResponse(PlanExercise ex) {
        return PlanExerciseResponse.builder()
                .id(ex.getId())
                .exerciseName(ex.getExerciseName())
                .sets(ex.getSets())
                .reps(ex.getReps())
                .restSeconds(ex.getRestSeconds())
                .dayOfWeek(ex.getDayOfWeek())
                .weekNumber(ex.getWeekNumber())
                .build();
    }
}
