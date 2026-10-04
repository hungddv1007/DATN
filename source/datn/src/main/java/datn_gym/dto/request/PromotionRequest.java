package datn_gym.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PromotionRequest {

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String code;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm tối thiểu là 1%")
    @Max(value = 100, message = "Phần trăm giảm tối đa là 100%")
    private Integer discountPercent;

    private Integer packageId; // Có thể null (áp dụng cho mọi gói)

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @Min(value = 1, message = "Số lượng sử dụng tối đa phải lớn hơn 0")
    private Integer maxUsage;
}
