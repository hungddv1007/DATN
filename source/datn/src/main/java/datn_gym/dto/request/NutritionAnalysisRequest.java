package datn_gym.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NutritionAnalysisRequest {
    @Size(max = 1000, message = "Mỗi bữa ăn không được vượt quá 1000 ký tự")
    private String breakfastMeal;
    @Size(max = 1000, message = "Mỗi bữa ăn không được vượt quá 1000 ký tự")
    private String preworkoutMeal;
    @Size(max = 1000, message = "Mỗi bữa ăn không được vượt quá 1000 ký tự")
    private String lunchMeal;
    @Size(max = 1000, message = "Mỗi bữa ăn không được vượt quá 1000 ký tự")
    private String postworkoutMeal;
    @Size(max = 1000, message = "Mỗi bữa ăn không được vượt quá 1000 ký tự")
    private String dinnerMeal;

    @Pattern(
            regexp = "TRAINING_DAY|REST_DAY",
            message = "Loại ngày phải là TRAINING_DAY hoặc REST_DAY")
    private String dayType; // TRAINING_DAY or REST_DAY
}
