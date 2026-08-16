package datn_gym.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CompleteScheduleRequest {
    @Size(max = 1000) private String actualNote;
    @NotEmpty(message = "Vui lòng ghi nhận ít nhất một bài tập")
    private List<@Valid ScheduleExerciseRequest> exercises;
}
