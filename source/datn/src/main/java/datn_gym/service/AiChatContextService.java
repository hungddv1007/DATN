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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.StringJoiner;

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
        appendPhysicalProfile(context, member.getId(), includePhysicalData);

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
            String physicalProfile = memberProfileRepository.findByUser_Id(member.getId())
                    .map(this::formatPhysicalProfile)
                    .orElse("Chưa có thông tin");
            response.append("- **Hồ sơ thể chất:** ")
                    .append(physicalProfile)
                    .append('.');
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

    private void appendPhysicalProfile(
            StringBuilder context,
            Integer memberId,
            boolean includePhysicalData) {
        if (!includePhysicalData) {
            context.append("Hồ sơ thể chất: không được chia sẻ với AI theo lựa chọn của hội viên.\n");
            return;
        }

        String physicalProfile = memberProfileRepository.findByUser_Id(memberId)
                .map(this::formatPhysicalProfile)
                .orElse("chưa có thông tin");
        context.append("Hồ sơ thể chất do hội viên cung cấp: ")
                .append(physicalProfile)
                .append('\n');
    }

    private String formatPhysicalProfile(MemberProfile profile) {
        StringJoiner details = new StringJoiner("; ");
        addMetric(details, "chiều cao", profile.getHeightCm(), "cm");
        addMetric(details, "cân nặng", profile.getWeightKg(), "kg");
        if (profile.getDateOfBirth() != null) {
            addText(details, "tuổi", String.valueOf(
                    datn_gym.util.BodyFatEstimator.ageOn(
                            profile.getDateOfBirth(), LocalDate.now())));
        }
        addText(details, "giới tính sinh học", switch (
                profile.getBiologicalSex() == null ? "" : profile.getBiologicalSex()) {
            case "MALE" -> "nam";
            case "FEMALE" -> "nữ";
            default -> null;
        });
        addMetric(details, "vòng ngực", profile.getChestCm(), "cm");
        addMetric(details, "vòng eo", profile.getWaistCm(), "cm");
        addMetric(details, "vòng hông", profile.getHipCm(), "cm");
        addMetric(details,
                "tỷ lệ mỡ" + ("ESTIMATED".equals(profile.getBodyFatSource())
                        ? " ước tính"
                        : ""),
                profile.getBodyFatPercentage(), "%");
        addText(details, "mức vận động", activityLevelLabel(profile.getActivityLevel()));
        addText(details, "mục tiêu", fitnessGoalLabel(profile.getFitnessGoal()));
        addMetric(details, "cân nặng mục tiêu", profile.getTargetWeightKg(), "kg");
        addText(details, "kinh nghiệm tập luyện", profile.getTrainingExperience());
        addText(details, "tiền sử chấn thương", profile.getInjuryHistory());
        addText(details, "bệnh lý hoặc hạn chế vận động", profile.getMedicalConditions());
        return details.length() == 0 ? "chưa có thông tin" : details.toString();
    }

    private void addMetric(
            StringJoiner details,
            String label,
            BigDecimal value,
            String unit) {
        if (value != null) {
            details.add(label + ": " + value.stripTrailingZeros().toPlainString() + " " + unit);
        }
    }

    private void addText(StringJoiner details, String label, String value) {
        if (value != null && !value.isBlank()) {
            details.add(label + ": " + value.trim());
        }
    }

    private String activityLevelLabel(String value) {
        if (value == null) return null;
        return switch (value) {
            case "SEDENTARY" -> "ít vận động";
            case "LIGHT" -> "vận động nhẹ";
            case "MODERATE" -> "vận động vừa";
            case "HIGH" -> "vận động nhiều";
            case "VERY_HIGH" -> "vận động cường độ rất cao";
            default -> value;
        };
    }

    private String fitnessGoalLabel(String value) {
        if (value == null) return null;
        return switch (value) {
            case "WEIGHT_LOSS" -> "giảm cân";
            case "MUSCLE_GAIN" -> "tăng cơ";
            case "MAINTENANCE" -> "duy trì vóc dáng";
            case "HEALTH_IMPROVEMENT" -> "cải thiện sức khỏe";
            default -> value;
        };
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
