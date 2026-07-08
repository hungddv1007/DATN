package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PackageDiscountRequest {
    
    // ID gói tập áp dụng (null = tất cả)
    private Integer packageId;

    @NotNull(message = "Vui lòng nhập số ngày tối thiểu")
    private Integer minDays;

    @NotNull(message = "Vui lòng nhập phần trăm giảm giá")
    private Integer discountPercent;
}
