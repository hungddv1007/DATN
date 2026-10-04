package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AiConversationResponse {
    private Integer id;
    private String title;
    private boolean physicalDataConsent;
    private boolean saleDataConsent;
    private String handoffStatus;
    private Integer assignedSaleId;
    private String assignedSaleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime handoffAt;
}
