package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionAnalysisResponse {
    private int totalCalories;
    private int totalProtein;
    private int totalCarbs;
    private int totalFat;
    private List<MealNutrition> meals;
    private String aiNote;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealNutrition {
        private String mealName;
        private int calories;
        private int protein;
        private int carbs;
        private int fat;
    }
}
