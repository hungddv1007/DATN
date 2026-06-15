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

    @NotNull(message = "Giá gói tập không được để trống")
    @Min(value = 0, message = "Giá gói tập phải >= 0")
    private BigDecimal price;

    @NotNull(message = "Số ngày sử dụng không được để trống")
    @Min(value = 1, message = "Số ngày sử dụng phải >= 1")
    private Integer durationDays;

    private String description;

    private Boolean hasPt;
    private Boolean canChoosePt;
    private Boolean hasMealPlan;
}
