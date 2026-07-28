package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PackageDiscountRequest {

    private Integer packageId;

    @NotNull(message = "Số ngày tối thiểu không được để trống")
    @Min(value = 1, message = "Số ngày tối thiểu phải >= 1")
    private Integer minDays;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "% giảm giá phải từ 1 đến 100")
    @Max(value = 100, message = "% giảm giá phải từ 1 đến 100")
    private Integer discountPercent;
}
