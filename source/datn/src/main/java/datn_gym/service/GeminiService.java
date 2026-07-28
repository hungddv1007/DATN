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

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

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
            - Trả về JSON hợp lệ theo đúng format sau, không thêm bất kỳ văn bản nào khác:

            {
              "total": {
                "calories": <số nguyên>,
                "protein": <số nguyên>,
                "carbs": <số nguyên>,
                "fat": <số nguyên>
              },
              "meals": [
                {
                  "name": "<tên bữa>",
                  "calories": <số nguyên>,
                  "protein": <số nguyên>,
                  "carbs": <số nguyên>,
                  "fat": <số nguyên>
                }
              ],
              "note": "<lời khuyên/ghi chú ngắn 1-2 câu dành cho học viên>"
            }
            """, dayTypeName, mealText, noteInstruction);

        try {
            String rawText = aiClient.generateJson(prompt);
            String jsonText = extractAndRepairJson(rawText);
            JsonNode rootNode = objectMapper.readTree(jsonText);

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
            log.warn("AI trả về JSON dinh dưỡng không hợp lệ", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI trả về dữ liệu không hợp lệ. Vui lòng thử lại.");
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private String extractAndRepairJson(String text) {
        if (text == null) return "{}";
        int firstBrace = text.indexOf('{');
        if (firstBrace == -1) return text.trim();

        int lastBrace = text.lastIndexOf('}');
        String json;
        if (lastBrace > firstBrace) {
            json = text.substring(firstBrace, lastBrace + 1);
        } else {
            json = text.substring(firstBrace);
        }

        // Tự động sửa lỗi JSON nếu bị ngắt dòng chừng (Unclosed brackets/braces)
        return repairJson(json);
    }

    private String repairJson(String json) {
        if (json == null || json.isEmpty()) return "{}";
        json = json.trim();

        int openBraces = 0;
        int openBrackets = 0;
        boolean inString = false;
        boolean escape = false;

        StringBuilder repaired = new StringBuilder();
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            repaired.append(c);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (!inString) {
                if (c == '{') openBraces++;
                else if (c == '}') openBraces--;
                else if (c == '[') openBrackets++;
                else if (c == ']') openBrackets--;
            }
        }

        if (inString) {
            repaired.append('"');
        }

        String current = repaired.toString().trim();
        if (current.endsWith(",")) {
            current = current.substring(0, current.length() - 1);
            repaired = new StringBuilder(current);
        }

        while (openBrackets > 0) {
            repaired.append("]");
            openBrackets--;
        }
        while (openBraces > 0) {
            repaired.append("}");
            openBraces--;
        }

        return repaired.toString();
    }
}
