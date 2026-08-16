package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @Builder
public class CommissionResponse {
    private Long id; private Integer transactionId; private String memberName; private BigDecimal baseAmount;
    private Integer commissionRate; private BigDecimal commissionAmount; private String status;
    private LocalDateTime createdAt; private LocalDateTime paidAt;
}
