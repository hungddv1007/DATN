package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SessionResponse {
    private Integer id;
    private Integer weekNum;
    private Integer dayNum;
    private String name;
    private Boolean isRestDay;

    // Danh sách bài tập trong buổi (rỗng nếu là rest day)
    private List<SessionExerciseResponse> exercises;
}
