package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PromotionResponse {
    private Integer id;
    private String code;
    private Integer discountPercent;
    private Integer packageId;
    private String packageName; // Tên gói nếu áp dụng riêng
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxUsage;
    private Integer currentUsage;
    private Boolean isActive;
    
    // Status helper cho FE (Sắp diễn ra, Đang diễn ra, Đã kết thúc, Hết lượt)
    private String status; 
}
