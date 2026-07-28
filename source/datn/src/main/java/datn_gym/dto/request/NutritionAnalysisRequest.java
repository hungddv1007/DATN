package datn_gym.dto.request;

import lombok.Data;

@Data
public class NutritionAnalysisRequest {
    private String breakfastMeal;
    private String preworkoutMeal;
    private String lunchMeal;
    private String postworkoutMeal;
    private String dinnerMeal;
    private String dayType; // TRAINING_DAY or REST_DAY
}
