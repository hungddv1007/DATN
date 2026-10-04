package datn_gym.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GymPackageRequest {

    @NotBlank(message = "Tên gói tập không được để trống")
    @Size(max = 50, message = "Tên gói tập tối đa 50 ký tự")
    private String name;

    @NotNull(message = "Đơn giá/ngày không được để trống")
    @Min(value = 0, message = "Đơn giá phải >= 0")
    private BigDecimal dailyPrice;

    private String description;

    @Min(value = 1, message = "Số ngày tối thiểu phải >= 1")
    private Integer minDays = 1;

    private Boolean hasPt;
    private Boolean canChoosePt;
    private Boolean hasMealPlan;

    // Bảo lưu
    private Integer maxHoldTimes = 0;
    private Integer holdReturnPercent = 0;
}
