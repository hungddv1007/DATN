package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class GymPackageResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer durationDays;
    private String description;
    private Boolean hasPt;
    private Boolean canChoosePt;
    private Boolean hasMealPlan;
    private Boolean isActive;
}
