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
    private String packageName;
    private BigDecimal dailyPrice; // snapshot
    private Integer durationDays; // snapshot
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String ptName;
    private LocalDateTime createdAt;
    
    // Hold info
    private Integer holdCount;
    private LocalDate pausedAt;
    private Integer totalHoldDays;

    // Transaction info
    private Integer transactionId;
    private BigDecimal originalAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String transactionStatus;
    private String transactionType;
    private String promotionCode;
    private Integer discountPercent;
}
