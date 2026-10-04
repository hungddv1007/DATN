package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MomoPaymentResponse {
    private Integer transactionId;
    private String orderId;
    private BigDecimal amount;
    private String transactionStatus;
    private String payUrl;
    private String deeplink;
    private String qrCode;
    private Integer resultCode;
    private String message;
    private LocalDateTime expiresAt;
    private LocalDateTime paidAt;
}
