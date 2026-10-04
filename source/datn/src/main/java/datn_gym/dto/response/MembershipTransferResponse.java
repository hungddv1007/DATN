package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class MembershipTransferResponse {
    private Long id; private Integer sourceMembershipId; private Integer senderId; private String senderName;
    private Integer recipientId; private String recipientName; private String recipientEmail; private String packageName;
    private Integer remainingDays; private Integer estimatedDeductionDays; private Integer transferredDays; private String status;
    private boolean replacesCurrentPackage; private String recipientCurrentPackage; private Integer recipientCurrentRemainingDays;
    private LocalDateTime expiresAt; private LocalDateTime createdAt; private LocalDateTime acceptedAt;
}
