package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ServiceReviewRequest {
    @NotNull private Integer transactionId;
    @NotNull @Min(1) @Max(5) private Integer ratingStar;
    @NotBlank @Size(max = 2000) private String comment;
    private boolean displayName = true;
}
