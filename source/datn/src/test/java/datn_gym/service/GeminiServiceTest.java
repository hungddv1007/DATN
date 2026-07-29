package datn_gym.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.ai.AiClient;
import datn_gym.dto.request.NutritionAnalysisRequest;
import datn_gym.dto.response.NutritionAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private AiClient aiClient;

    private GeminiService service;

    @BeforeEach
    void setUp() {
        service = new GeminiService(new ObjectMapper(), aiClient);
    }

    @Test
    void analyzeNutritionUsesSchemaAndMapsValidResponse() {
        when(aiClient.generateStructuredJson(anyString(), any()))
                .thenReturn("""
                        {
                          "total":{"calories":1800,"protein":140,"carbs":190,"fat":50},
                          "meals":[
                            {"name":"Bữa sáng","calories":500,"protein":35,"carbs":60,"fat":12}
                          ],
                          "note":"Uống đủ nước."
                        }
                        """);

        NutritionAnalysisResponse response =
                service.analyzeNutrition(trainingDayRequest());

        assertThat(response.getTotalCalories()).isEqualTo(1800);
        assertThat(response.getTotalProtein()).isEqualTo(140);
        assertThat(response.getMeals()).hasSize(1);
        assertThat(response.getAiNote()).isEqualTo("Uống đủ nước.");
        verify(aiClient).generateStructuredJson(anyString(), any());
    }

    @Test
    void analyzeNutritionRejectsSemanticallyInvalidNumbers() {
        when(aiClient.generateStructuredJson(anyString(), any()))
                .thenReturn("""
                        {
                          "total":{"calories":999999,"protein":140,"carbs":190,"fat":50},
                          "meals":[
                            {"name":"Bữa sáng","calories":500,"protein":35,"carbs":60,"fat":12}
                          ],
                          "note":"Dữ liệu lỗi."
                        }
                        """);

        assertThatThrownBy(() -> service.analyzeNutrition(trainingDayRequest()))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode())
                                .isEqualTo(HttpStatus.BAD_GATEWAY));
    }

    private NutritionAnalysisRequest trainingDayRequest() {
        NutritionAnalysisRequest request = new NutritionAnalysisRequest();
        request.setDayType("TRAINING_DAY");
        request.setBreakfastMeal("100g yến mạch, 2 quả trứng");
        return request;
    }
}
