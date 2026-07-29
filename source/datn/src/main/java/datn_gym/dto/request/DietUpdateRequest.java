package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DietUpdateRequest {

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
