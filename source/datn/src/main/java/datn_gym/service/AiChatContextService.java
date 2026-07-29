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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatContextService {

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    @Transactional(readOnly = true)
    public String buildAccountStatusResponse(
            String email,
            boolean includePhysicalData) {
        User member = userService.getUserByEmail(email);
        LocalDate today = LocalDate.now();
        Membership membership = findActiveMembership(member.getId(), today);
        List<PtSchedule> schedules = ptScheduleRepository
                .findByMemberIdAndScheduleDateBetweenAndStatusOrderByScheduleDateAscStartTimeAsc(
                        member.getId(),
                        today,
                        today.plusDays(7),
                        "ACTIVE");
        List<Diet> diets =
                dietRepository.findByMember_IdOrderByCreatedAtDesc(member.getId());

        StringBuilder response = new StringBuilder()
                .append("Chào ").append(member.getFullName())
                .append(". Đây là trạng thái hiện tại được lấy trực tiếp từ GymPro:\n\n");

        if (membership == null) {
            response.append("- **Gói tập:** Chưa có gói đang hoạt động.\n")
                    .append("- **PT phụ trách:** Chưa được phân công.\n");
        } else {
            response.append("- **Gói tập:** ")
                    .append(membership.getGymPackage().getName())
                    .append('\n')
                    .append("- **Thời hạn:** ")
                    .append(membership.getStartDate().format(DISPLAY_DATE))
                    .append(" đến ")
                    .append(membership.getEndDate().format(DISPLAY_DATE))
                    .append(" (còn ")
                    .append(Math.max(0, ChronoUnit.DAYS.between(
                            today,
                            membership.getEndDate())))
                    .append(" ngày)\n")
                    .append("- **PT phụ trách:** ")
                    .append(membership.getPt() != null
                            ? membership.getPt().getFullName()
                            : "Chưa được phân công")
                    .append('\n');
        }

        if (schedules.isEmpty()) {
            response.append("- **Lịch tập 7 ngày tới:** Chưa có lịch.\n");
        } else {
            response.append("- **Lịch tập 7 ngày tới:**\n");
            schedules.stream().limit(10).forEach(schedule -> response
                    .append("  - ")
                    .append(schedule.getScheduleDate().format(DISPLAY_DATE))
                    .append(' ')
                    .append(schedule.getStartTime())
                    .append('-')
                    .append(schedule.getEndTime())
                    .append(": ")
                    .append(textOrFallback(
                            schedule.getExerciseNote(),
                            "Buổi tập với PT"))
                    .append('\n'));
        }

        if (diets.isEmpty()) {
            response.append("- **Thực đơn:** PT chưa thiết lập.\n");
        } else {
            Diet latestDiet = diets.get(0);
            response.append("- **Thực đơn gần nhất:** ")
                    .append(textOrFallback(latestDiet.getTitle(), "Thực đơn"))
                    .append(" — ")
                    .append(latestDiet.getCalories()).append(" kcal, ")
                    .append(latestDiet.getProteinG()).append("g protein, ")
                    .append(latestDiet.getCarbsG()).append("g carbs, ")
                    .append(latestDiet.getFatG()).append("g fat.\n");
        }

        if (!includePhysicalData) {
            response.append("- **Hồ sơ thể chất:** Chưa được chia sẻ theo lựa chọn của bạn.");
        } else {
            String condition = memberProfileRepository.findByUser_Id(member.getId())
                    .map(MemberProfile::getPhysicalCondition)
                    .filter(value -> !value.isBlank())
                    .orElse("Chưa có thông tin");
            response.append("- **Hồ sơ thể chất:** ").append(condition).append('.');
        }

        return response.toString();
    }

    private void appendMembership(
            StringBuilder context,
            Integer memberId,
            LocalDate today) {
        Membership activeMembership = findActiveMembership(memberId, today);

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

    private Membership findActiveMembership(Integer memberId, LocalDate today) {
        return membershipRepository
                .findByUser_IdOrderByCreatedAtDesc(memberId)
                .stream()
                .filter(membership -> "ACTIVE".equals(membership.getStatus())
                        && !membership.getEndDate().isBefore(today))
                .findFirst()
                .orElse(null);
    }

    private String textOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
