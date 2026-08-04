package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MemberProfileResponse {

    // Từ bảng users
    private Integer id;          // user_id
    private String fullName;
    private String email;
    private String phone;
    private String avatar;

    // Các trường hồ sơ thể chất đều có thể để trống
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private LocalDate dateOfBirth;
    private String biologicalSex;
    private BigDecimal chestCm;
    private BigDecimal waistCm;
    private BigDecimal hipCm;
    private BigDecimal bodyFatPercentage;
    private String bodyFatSource;
    private String activityLevel;
    private String fitnessGoal;
    private BigDecimal targetWeightKg;
    private String trainingExperience;
    private String injuryHistory;
    private String medicalConditions;
}
