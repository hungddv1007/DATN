package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AiDietGenerationRequest {

    @NotNull(message = "Hội viên không được để trống")
    private Integer memberId;

    @NotBlank(message = "Loại ngày không được để trống")
    @Pattern(
            regexp = "TRAINING_DAY|REST_DAY",
            message = "Loại ngày phải là TRAINING_DAY hoặc REST_DAY")
    private String dayType;
}
