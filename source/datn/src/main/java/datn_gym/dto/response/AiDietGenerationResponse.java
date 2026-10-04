package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDietGenerationResponse {

    private String title;
    private String breakfast;
    private String snackMorning;
    private String lunch;
    private String snackAfternoon;
    private String dinner;
    private String note;
}
