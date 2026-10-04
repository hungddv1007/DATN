package datn_gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SaleCodeRequest {
    @NotBlank @Size(min = 4, max = 50) private String code;
    @Size(max = 255) private String description;
    private boolean oneTimePerMember;
    private LocalDateTime expiresAt;
}
