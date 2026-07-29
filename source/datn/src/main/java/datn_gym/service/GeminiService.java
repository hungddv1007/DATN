package datn_gym.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.ai.AiClient;
import datn_gym.dto.request.NutritionAnalysisRequest;
import datn_gym.dto.response.NutritionAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private static final int MAX_REASONABLE_CALORIES = 20_000;
    private static final int MAX_REASONABLE_MACRO_GRAMS = 2_000;

    private static final Map<String, Object> NUTRITION_SCHEMA = Map.of(
            "type", "object",
            "properties", Map.of(
                    "total", Map.of(
                            "type", "object",
                            "properties", nutritionProperties(),
                            "required", List.of("calories", "protein", "carbs", "fat")
                    ),
                    "meals", Map.of(
                            "type", "array",
                            "items", Map.of(
                                    "type", "object",
                                    "properties", mealProperties(),
                                    "required", List.of(
                                            "name", "calories", "protein", "carbs", "fat")
                            )
                    ),
                    "note", Map.of(
                            "type", "string",
                            "description", "Lời khuyên ngắn gọn 1-2 câu bằng tiếng Việt"
                    )
            ),
            "required", List.of("total", "meals", "note")
    );

    private final ObjectMapper objectMapper;
    private final AiClient aiClient;

    public NutritionAnalysisResponse analyzeNutrition(NutritionAnalysisRequest request) {
        boolean isRestDay = "REST_DAY".equalsIgnoreCase(request.getDayType());
        String dayTypeName = isRestDay ? "NGÀY NGHỈ (Rest Day)" : "NGÀY TẬP (Training Day)";

        StringBuilder mealTextBuilder = new StringBuilder();
        mealTextBuilder.append("Loại ngày: ").append(dayTypeName).append("\n");

        if (hasText(request.getBreakfastMeal())) {
            mealTextBuilder.append("Bữa sáng: ").append(request.getBreakfastMeal()).append("\n");
        }
        if (!isRestDay && hasText(request.getPreworkoutMeal())) {
            mealTextBuilder.append("Bữa phụ sáng / Pre-workout: ").append(request.getPreworkoutMeal()).append("\n");
        }
        if (hasText(request.getLunchMeal())) {
            mealTextBuilder.append("Bữa trưa: ").append(request.getLunchMeal()).append("\n");
        }
        if (!isRestDay && hasText(request.getPostworkoutMeal())) {
            mealTextBuilder.append("Bữa phụ chiều / Post-workout: ").append(request.getPostworkoutMeal()).append("\n");
        }
        if (hasText(request.getDinnerMeal())) {
            mealTextBuilder.append("Bữa tối: ").append(request.getDinnerMeal()).append("\n");
        }

        String mealText = mealTextBuilder.toString().trim();
        if (mealText.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập ít nhất một bữa ăn");
        }

        String noteInstruction = isRestDay
            ? "Đưa ra 1-2 câu lời khuyên ngắn gọn cho học viên dành riêng cho NGÀY NGHỈ (tập trung vào nghỉ ngơi phục hồi cơ bắp, lượng nước uống, duy trì lượng protein và kiểm soát calo khi không vận động nặng)."
            : "Đưa ra 1-2 câu lời khuyên ngắn gọn dành cho NGÀY TẬP (nạp đủ năng lượng pre/post-workout, bù nước và phân bổ dinh dưỡng phục vụ tập luyện).";

        String prompt = String.format("""
            Bạn là chuyên gia dinh dưỡng thể hình. Hãy phân tích chỉ số dinh dưỡng của thực đơn sau dành cho %s:

            %s

            Yêu cầu:
            - Tính calo (kcal), protein (g), carbs (g), fat (g) cho từng bữa và tổng ngày.
            - Viết súc tích, ngắn gọn.
            - Mục "note": %s
            - Chỉ trả về dữ liệu theo JSON Schema đã được cung cấp.
            """, dayTypeName, mealText, noteInstruction);

        try {
            String rawText = aiClient.generateStructuredJson(prompt, NUTRITION_SCHEMA);
            JsonNode rootNode = objectMapper.readTree(rawText);

            JsonNode totalNode = rootNode.path("total");
            int totalCalories = totalNode.path("calories").asInt(0);
            int totalProtein = totalNode.path("protein").asInt(0);
            int totalCarbs = totalNode.path("carbs").asInt(0);
            int totalFat = totalNode.path("fat").asInt(0);

            String aiNote = rootNode.path("note").asText("");

            List<NutritionAnalysisResponse.MealNutrition> mealList = new ArrayList<>();
            JsonNode mealsArray = rootNode.path("meals");
            if (mealsArray.isArray()) {
                for (JsonNode m : mealsArray) {
                    mealList.add(NutritionAnalysisResponse.MealNutrition.builder()
                        .mealName(m.path("name").asText("Bữa ăn"))
                        .calories(m.path("calories").asInt(0))
                        .protein(m.path("protein").asInt(0))
                        .carbs(m.path("carbs").asInt(0))
                        .fat(m.path("fat").asInt(0))
                        .build());
                }
            }
            validateNutritionResult(
                    totalCalories,
                    totalProtein,
                    totalCarbs,
                    totalFat,
                    mealList);

            return NutritionAnalysisResponse.builder()
                .totalCalories(totalCalories)
                .totalProtein(totalProtein)
                .totalCarbs(totalCarbs)
                .totalFat(totalFat)
                .meals(mealList)
                .aiNote(aiNote)
                .build();

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.warn("AI trả về JSON dinh dưỡng không hợp lệ: {}", e.getMessage());
            log.debug("Chi tiết lỗi phản hồi dinh dưỡng từ AI", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI trả về dữ liệu không hợp lệ. Vui lòng thử lại.");
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private void validateNutritionResult(
            int calories,
            int protein,
            int carbs,
            int fat,
            List<NutritionAnalysisResponse.MealNutrition> meals) {
        if (meals.isEmpty()) {
            throw new IllegalStateException("AI không trả về dinh dưỡng cho từng bữa.");
        }
        validateNutritionNumbers(calories, protein, carbs, fat);
        meals.forEach(meal -> validateNutritionNumbers(
                meal.getCalories(),
                meal.getProtein(),
                meal.getCarbs(),
                meal.getFat()));
    }

    private void validateNutritionNumbers(int calories, int protein, int carbs, int fat) {
        if (calories < 0 || calories > MAX_REASONABLE_CALORIES
                || protein < 0 || protein > MAX_REASONABLE_MACRO_GRAMS
                || carbs < 0 || carbs > MAX_REASONABLE_MACRO_GRAMS
                || fat < 0 || fat > MAX_REASONABLE_MACRO_GRAMS) {
            throw new IllegalStateException("AI trả về chỉ số dinh dưỡng ngoài phạm vi hợp lý.");
        }
    }

    private static Map<String, Object> nutritionProperties() {
        return Map.of(
                "calories", integerField("Năng lượng tính bằng kcal"),
                "protein", integerField("Protein tính bằng gram"),
                "carbs", integerField("Carbohydrate tính bằng gram"),
                "fat", integerField("Chất béo tính bằng gram")
        );
    }

    private static Map<String, Object> mealProperties() {
        return Map.of(
                "name", Map.of("type", "string"),
                "calories", integerField("Năng lượng của bữa ăn tính bằng kcal"),
                "protein", integerField("Protein của bữa ăn tính bằng gram"),
                "carbs", integerField("Carbohydrate của bữa ăn tính bằng gram"),
                "fat", integerField("Chất béo của bữa ăn tính bằng gram")
        );
    }

    private static Map<String, Object> integerField(String description) {
        return Map.of(
                "type", "integer",
                "minimum", 0,
                "description", description
        );
    }
}
