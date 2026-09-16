package datn_gym.service;

import datn_gym.entity.Diet;
import datn_gym.entity.GymPackage;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.Membership;
import datn_gym.entity.PtProfile;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.PtScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
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
    private final GymPackageRepository gymPackageRepository;
    private final PtProfileRepository ptProfileRepository;

    @Transactional(readOnly = true)
    public String buildMemberContext(String email, boolean includePhysicalData) {
        return buildMemberContext(email, includePhysicalData, "");
    }

    @Transactional(readOnly = true)
    public String buildMemberContext(
            String email,
            boolean includePhysicalData,
            String userQuestion) {
        User member = userService.getUserByEmail(email);
        LocalDate today = LocalDate.now();
        StringBuilder context = new StringBuilder();

        context.append("Tên hội viên: ").append(member.getFullName()).append('\n');
        appendMembership(context, member.getId(), today);
        appendSchedule(context, member.getId(), today);
        appendDiet(context, member.getId());
        appendPhysicalProfile(context, member.getId(), includePhysicalData);
        if (shouldIncludePackageAndPtCatalog(userQuestion)) {
            appendActivePackages(context);
            appendAvailablePts(context);
        }

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

    private void appendActivePackages(StringBuilder context) {
        List<GymPackage> packages = gymPackageRepository.findByIsActiveTrue();
        context.append("\nDANH SÁCH GÓI TẬP ĐANG MỞ BÁN:\n");

        if (packages.isEmpty()) {
            context.append("- Hiện chưa có gói tập đang mở bán.\n");
            return;
        }

        packages.stream().limit(20).forEach(gymPackage -> {
            context.append("- Tên gói: ")
                    .append(gymPackage.getName())
                    .append('\n')
                    .append("  Giá cơ bản: ")
                    .append(gymPackage.getDailyPrice().stripTrailingZeros().toPlainString())
                    .append(" VND/ngày\n")
                    .append("  Thời hạn đăng ký tối thiểu: ")
                    .append(gymPackage.getMinDays())
                    .append(" ngày\n")
                    .append("  Có PT: ")
                    .append(yesNo(gymPackage.getHasPt()))
                    .append('\n')
                    .append("  Được tự chọn PT: ")
                    .append(yesNo(gymPackage.getCanChoosePt()))
                    .append('\n')
                    .append("  Có khẩu phần ăn: ")
                    .append(yesNo(gymPackage.getHasMealPlan()))
                    .append('\n');

            if (gymPackage.getMaxHoldTimes() != null
                    && gymPackage.getMaxHoldTimes() > 0) {
                context.append("  Bảo lưu tối đa: ")
                        .append(gymPackage.getMaxHoldTimes())
                        .append(" lần; tỷ lệ ngày được hoàn lại: ")
                        .append(gymPackage.getHoldReturnPercent())
                        .append("%\n");
            } else {
                context.append("  Không hỗ trợ bảo lưu.\n");
            }

            context.append("  Mô tả: ")
                    .append(catalogText(gymPackage.getDescription(), "Chưa có mô tả"))
                    .append('\n');
        });

        context.append("Giá trên là giá cơ bản theo ngày. Giá thanh toán cuối cùng còn phụ thuộc ")
                .append("thời hạn, ưu đãi, mã khuyến mãi, mã giới thiệu và credit nâng cấp.\n");
    }

    private void appendAvailablePts(StringBuilder context) {
        List<PtProfile> profiles = ptProfileRepository.findAllOrderByRatingScoreDesc();
        context.append("\nDANH SÁCH PT CÒN KHẢ NĂNG NHẬN HỌC VIÊN:\n");
        boolean hasAvailablePt = false;

        for (PtProfile profile : profiles.stream().limit(50).toList()) {
            User pt = profile.getUser();
            if (pt == null || !Boolean.TRUE.equals(pt.getStatus())) {
                continue;
            }

            int currentMembers = membershipRepository
                    .countByPt_IdAndStatus(pt.getId(), "ACTIVE");
            int maxMembers = profile.getMaxMembers() != null
                    ? profile.getMaxMembers()
                    : 5;
            if (currentMembers >= maxMembers) {
                continue;
            }

            hasAvailablePt = true;
            String rating = profile.getRatingScore() == null
                    ? "Chưa có đánh giá"
                    : profile.getRatingScore()
                            .setScale(1, RoundingMode.HALF_UP)
                            .toPlainString() + "/5";

            context.append("- ")
                    .append(pt.getFullName())
                    .append('\n')
                    .append("  Chuyên môn: ")
                    .append(catalogText(profile.getSpecialization(), "Chưa cập nhật"))
                    .append('\n')
                    .append("  Đánh giá: ")
                    .append(rating)
                    .append('\n')
                    .append("  Số học viên: ")
                    .append(currentMembers)
                    .append('/')
                    .append(maxMembers)
                    .append('\n');

            if (profile.getCertificates() != null && !profile.getCertificates().isBlank()) {
                context.append("  Chứng chỉ: ")
                        .append(catalogText(profile.getCertificates(), ""))
                        .append('\n');
            }
            if (profile.getBio() != null && !profile.getBio().isBlank()) {
                context.append("  Giới thiệu: ")
                        .append(catalogText(profile.getBio(), ""))
                        .append('\n');
            }
        }

        if (!hasAvailablePt) {
            context.append("- Hiện chưa có PT còn chỗ nhận học viên.\n");
        }
        context.append("Danh sách trên chỉ dùng để tư vấn; PT chỉ được giữ chỗ và phân công ")
                .append("sau khi hội viên hoàn tất quy trình đăng ký hợp lệ.\n");
    }

    boolean shouldIncludePackageAndPtCatalog(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        String normalized = Normalizer.normalize(
                        question.toLowerCase(Locale.ROOT),
                        Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replaceAll("\\s+", " ")
                .trim();
        String padded = " " + normalized + " ";

        return normalized.contains("goi tap")
                || normalized.contains("cac goi")
                || normalized.contains("goi nao")
                || normalized.contains("goi basic")
                || normalized.contains("goi premium")
                || normalized.contains("goi vip")
                || normalized.contains("dang ky goi")
                || normalized.contains("mua goi")
                || normalized.contains("gia goi")
                || normalized.contains("quyen loi")
                || normalized.contains("huan luyen vien")
                || normalized.contains("trainer")
                || normalized.contains("coach")
                || padded.contains(" pt ")
                || padded.contains(" hlv ")
                || normalized.contains("goi y pt")
                || normalized.contains("chon pt");
    }

    private String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Có" : "Không";
    }

    private String catalogText(String value, String fallback) {
        String text = textOrFallback(value, fallback);
        return text.length() <= 500 ? text : text.substring(0, 500);
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
