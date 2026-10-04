package datn_gym.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PackageDiscountResponse {

    private Integer id;
    private Integer packageId;
    private String packageName;
    private Integer minDays;
    private Integer discountPercent;
}
