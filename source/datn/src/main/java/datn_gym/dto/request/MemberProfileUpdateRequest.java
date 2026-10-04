package datn_gym.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MemberProfileUpdateRequest {

    @DecimalMin(value = "50", message = "Chiều cao phải từ 50 cm")
    @DecimalMax(value = "300", message = "Chiều cao không vượt quá 300 cm")
    @Digits(integer = 3, fraction = 2, message = "Chiều cao chỉ được có tối đa 2 số thập phân")
    private BigDecimal heightCm;

    @DecimalMin(value = "20", message = "Cân nặng phải từ 20 kg")
    @DecimalMax(value = "500", message = "Cân nặng không vượt quá 500 kg")
    @Digits(integer = 3, fraction = 2, message = "Cân nặng chỉ được có tối đa 2 số thập phân")
    private BigDecimal weightKg;

    @Past(message = "Ngày sinh phải trước ngày hiện tại")
    private LocalDate dateOfBirth;

    @Pattern(
            regexp = "MALE|FEMALE",
            message = "Giới tính sinh học không hợp lệ")
    private String biologicalSex;

    @DecimalMin(value = "20", message = "Vòng ngực phải từ 20 cm")
    @DecimalMax(value = "300", message = "Vòng ngực không vượt quá 300 cm")
    @Digits(integer = 3, fraction = 2, message = "Vòng ngực chỉ được có tối đa 2 số thập phân")
    private BigDecimal chestCm;

    @DecimalMin(value = "20", message = "Vòng eo phải từ 20 cm")
    @DecimalMax(value = "300", message = "Vòng eo không vượt quá 300 cm")
    @Digits(integer = 3, fraction = 2, message = "Vòng eo chỉ được có tối đa 2 số thập phân")
    private BigDecimal waistCm;

    @DecimalMin(value = "20", message = "Vòng hông phải từ 20 cm")
    @DecimalMax(value = "300", message = "Vòng hông không vượt quá 300 cm")
    @Digits(integer = 3, fraction = 2, message = "Vòng hông chỉ được có tối đa 2 số thập phân")
    private BigDecimal hipCm;

    @DecimalMin(value = "0", message = "Tỷ lệ mỡ không được âm")
    @DecimalMax(value = "100", message = "Tỷ lệ mỡ không vượt quá 100%")
    @Digits(integer = 3, fraction = 2, message = "Tỷ lệ mỡ chỉ được có tối đa 2 số thập phân")
    private BigDecimal bodyFatPercentage;

    @Pattern(
            regexp = "SEDENTARY|LIGHT|MODERATE|HIGH|VERY_HIGH",
            message = "Mức độ vận động không hợp lệ")
    private String activityLevel;

    @Pattern(
            regexp = "WEIGHT_LOSS|MUSCLE_GAIN|MAINTENANCE|HEALTH_IMPROVEMENT",
            message = "Mục tiêu tập luyện không hợp lệ")
    private String fitnessGoal;

    @DecimalMin(value = "20", message = "Cân nặng mục tiêu phải từ 20 kg")
    @DecimalMax(value = "500", message = "Cân nặng mục tiêu không vượt quá 500 kg")
    @Digits(integer = 3, fraction = 2, message = "Cân nặng mục tiêu chỉ được có tối đa 2 số thập phân")
    private BigDecimal targetWeightKg;

    @Size(max = 500, message = "Kinh nghiệm tập luyện không vượt quá 500 ký tự")
    private String trainingExperience;

    @Size(max = 2000, message = "Tiền sử chấn thương không vượt quá 2000 ký tự")
    private String injuryHistory;

    @Size(max = 2000, message = "Bệnh lý hoặc hạn chế vận động không vượt quá 2000 ký tự")
    private String medicalConditions;
}
