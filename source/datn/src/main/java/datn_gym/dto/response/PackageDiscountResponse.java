package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PackageDiscountResponse {
    private Integer id;
    private Integer packageId;
    private String packageName; // Nullable
    private Integer minDays;
    private Integer discountPercent;
}
