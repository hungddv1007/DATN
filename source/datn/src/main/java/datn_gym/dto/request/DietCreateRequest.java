package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DietCreateRequest {

    @NotNull(message = "Member ID không được để trống")
    private Integer memberId;

    @NotBlank(message = "Loại ngày không được để trống")
    private String dayType; // TRAINING_DAY, REST_DAY, SPECIFIC_DATE

    // Chỉ bắt buộc khi dayType = SPECIFIC_DATE
    private LocalDate dietDate;

    private String title;

    private String breakfast;
    private String snackMorning;
    private String lunch;
    private String snackAfternoon;
    private String dinner;

    @Min(value = 0, message = "Calories không được âm")
    private Integer calories;

    @Min(value = 0, message = "Protein không được âm")
    private Integer proteinG;

    @Min(value = 0, message = "Carbs không được âm")
    private Integer carbsG;

    @Min(value = 0, message = "Fat không được âm")
    private Integer fatG;

    private String note;
}
