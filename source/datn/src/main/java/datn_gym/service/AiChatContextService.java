package datn_gym.service;

import datn_gym.entity.Diet;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.Membership;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatContextService {

    private final UserService userService;
    private final MembershipRepository membershipRepository;
    private final DietRepository dietRepository;
    private final PtScheduleRepository ptScheduleRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Transactional(readOnly = true)
    public String buildMemberContext(String email, boolean includePhysicalData) {
        User member = userService.getUserByEmail(email);
        LocalDate today = LocalDate.now();
        StringBuilder context = new StringBuilder();

        context.append("Tên hội viên: ").append(member.getFullName()).append('\n');
        appendMembership(context, member.getId(), today);
        appendSchedule(context, member.getId(), today);
        appendDiet(context, member.getId());
        appendPhysicalCondition(context, member.getId(), includePhysicalData);

        return context.toString().trim();
    }

    private void appendMembership(
            StringBuilder context,
            Integer memberId,
            LocalDate today) {
        Membership activeMembership = membershipRepository
                .findByUser_IdOrderByCreatedAtDesc(memberId)
                .stream()
                .filter(membership -> "ACTIVE".equals(membership.getStatus())
                        && !membership.getEndDate().isBefore(today))
                .findFirst()
                .orElse(null);

        if (activeMembership == null) {
            context.append("Gói tập hiện tại: chưa có gói đang hoạt động.\n");
            return;
        }

        context.append("Gói tập hiện tại: ")
                .append(activeMembership.getGymPackage().getName())
                .append(", từ ").append(activeMembership.getStartDate())
                .append(" đến ").append(activeMembership.getEndDate());
        if (activeMembership.getPt() != null) {
            context.append(", PT phụ trách: ")
                    .append(activeMembership.getPt().getFullName());
        }
        context.append(".\n");
    }

    private void appendSchedule(
            StringBuilder context,
            Integer memberId,
            LocalDate today) {
        List<PtSchedule> schedules = ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
                        memberId,
                        today,
                        today.plusDays(7),
                        "ACTIVE");
        if (schedules.isEmpty()) {
            context.append("Lịch tập 7 ngày tới: chưa có lịch.\n");
            return;
        }

        context.append("Lịch tập 7 ngày tới:\n");
        schedules.stream().limit(10).forEach(schedule -> context
                .append("- ")
                .append(schedule.getScheduleDate())
                .append(' ')
                .append(schedule.getStartTime())
                .append('-')
                .append(schedule.getEndTime())
                .append(": ")
                .append(textOrFallback(schedule.getExerciseNote(), "Buổi tập với PT"))
                .append('\n'));
    }

    private void appendDiet(StringBuilder context, Integer memberId) {
        List<Diet> diets =
                dietRepository.findByMember_IdOrderByCreatedAtDesc(memberId);
        if (diets.isEmpty()) {
            context.append("Thực đơn: PT chưa thiết lập.\n");
            return;
        }

        context.append("Các thực đơn gần nhất:\n");
        diets.stream().limit(3).forEach(diet -> context
                .append("- ")
                .append(diet.getDayType())
                .append(diet.getDietDate() != null ? " " + diet.getDietDate() : "")
                .append(": ")
                .append(textOrFallback(diet.getTitle(), "Thực đơn"))
                .append("; ")
                .append(diet.getCalories()).append(" kcal, ")
                .append(diet.getProteinG()).append("g protein, ")
                .append(diet.getCarbsG()).append("g carbs, ")
                .append(diet.getFatG()).append("g fat.\n"));
    }

    private void appendPhysicalCondition(
            StringBuilder context,
            Integer memberId,
            boolean includePhysicalData) {
        if (!includePhysicalData) {
            context.append("Hồ sơ thể chất: không được chia sẻ với AI theo lựa chọn của hội viên.\n");
            return;
        }

        String condition = memberProfileRepository.findByUser_Id(memberId)
                .map(MemberProfile::getPhysicalCondition)
                .filter(value -> !value.isBlank())
                .orElse("chưa có thông tin");
        context.append("Hồ sơ thể chất do PT ghi nhận: ")
                .append(condition)
                .append('\n');
    }

    private String textOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
