package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponse {
    private Integer id;

    // Thông tin Membership
    private Integer membershipId;
    private String memberName;
    private String memberEmail;
    private String packageName;

    // Thông tin giao dịch
    private BigDecimal originalAmount;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String confirmedByName;
    private LocalDateTime createdAt;

    // Khuyến mãi
    private String promotionCode;
    private Integer discountPercent;
}
