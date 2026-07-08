package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpgradeRequest {

    @NotNull(message = "Vui lòng chọn gói mới")
    private Integer newPackageId;

    // Nếu null hoặc 0: nâng cấp tại chỗ (giữ endDate)
    // Nếu > 0: nâng cấp + gia hạn thêm
    private Integer extraDays;

    private String promotionCode;
    private String paymentMethod;
    private Integer ptId; // Chọn PT (nếu gói mới có canChoosePt)
}
