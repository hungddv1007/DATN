package datn_gym.service;

import datn_gym.dto.request.AssignPlanRequest;
import datn_gym.dto.response.PlanAssignmentResponse;
import datn_gym.entity.PlanAssignment;
import datn_gym.entity.TrainingPlan;
import datn_gym.entity.User;
import datn_gym.repository.PlanAssignmentRepository;
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
public class PlanAssignmentService {

    private final PlanAssignmentRepository assignmentRepo;
    private final TrainingPlanRepository planRepo;
    private final UserRepository userRepo;

    // Lấy tất cả phân công của PT
    public List<PlanAssignmentResponse> getAllByPt(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        List<PlanAssignment> assignments = assignmentRepo.findByPtIdOrderByCreatedAtDesc(pt.getId());
        return assignments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Gán lộ trình cho nhiều member
    @Transactional
    public List<PlanAssignmentResponse> assign(AssignPlanRequest request, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        TrainingPlan plan = planRepo.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lộ trình!"));
        if (!plan.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền gán lộ trình này!");
        }

        List<PlanAssignmentResponse> results = new ArrayList<>();

        for (Integer memberId : request.getMemberIds()) {
            // Kiểm tra member tồn tại
            User member = userRepo.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy member ID: " + memberId));

            // Kiểm tra gán trùng ACTIVE
            if (assignmentRepo.existsByPlanIdAndMemberIdAndStatus(plan.getId(), memberId, "ACTIVE")) {
                continue; // Bỏ qua, không gán trùng
            }

            PlanAssignment assignment = PlanAssignment.builder()
                    .plan(plan)
                    .member(member)
                    .pt(pt)
                    .startDate(request.getStartDate())
                    .status("ACTIVE")
                    .note(request.getNote())
                    .build();

            assignmentRepo.save(assignment);
            results.add(toResponse(assignment));
        }

        return results;
    }

    // Thay đổi trạng thái phân công
    @Transactional
    public PlanAssignmentResponse changeStatus(Integer assignmentId, String newStatus, String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        PlanAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phân công!"));
        if (!assignment.getPt().getId().equals(pt.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền thay đổi phân công này!");
        }

        // Kiểm tra trạng thái hợp lệ
        String current = assignment.getStatus();
        if ("COMPLETED".equals(current)) {
            throw new IllegalArgumentException("Phân công đã hoàn thành, không thể thay đổi!");
        }

        if (!List.of("ACTIVE", "PAUSED", "COMPLETED").contains(newStatus)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus);
        }

        assignment.setStatus(newStatus);
        assignmentRepo.save(assignment);
        return toResponse(assignment);
    }

    // === Helper ===

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
    }

    private PlanAssignmentResponse toResponse(PlanAssignment a) {
        return PlanAssignmentResponse.builder()
                .id(a.getId())
                .planId(a.getPlan().getId())
                .planTitle(a.getPlan().getTitle())
                .memberId(a.getMember().getId())
                .memberName(a.getMember().getFullName())
                .memberAvatar(a.getMember().getAvatar())
                .memberGoal(null) // Có thể mở rộng nếu MemberProfile có goal
                .startDate(a.getStartDate())
                .status(a.getStatus())
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
