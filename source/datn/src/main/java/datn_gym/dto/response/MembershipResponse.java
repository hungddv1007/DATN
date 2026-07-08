package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MembershipResponse {
    private Integer id;
    private Integer packageId;
    private String packageName;
    private BigDecimal dailyPrice;
    private Integer durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String ptName;
    private Integer ptId;
    private LocalDateTime createdAt;

    // Bảo lưu
    private Integer holdCount;
    private Integer maxHoldTimes;
    private Integer holdReturnPercent;
    private Integer totalHoldDays;
    private LocalDate pausedAt;

    // Thông tin giao dịch (mới nhất)
    private Integer transactionId;
    private String transactionType;
    private BigDecimal originalAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String transactionStatus;
    private String promotionCode;
    private Integer discountPercent;

    // Tính toán
    private Long remainingDays;
}
