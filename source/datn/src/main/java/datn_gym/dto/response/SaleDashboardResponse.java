package datn_gym.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data @Builder
public class SaleDashboardResponse {
    private Integer level; private Integer successfulCustomers; private Integer nextLevelTarget;
    private Integer discountPercent; private Integer commissionRate; private long activeCodes;
    private long activeChats; private BigDecimal pendingCommission; private BigDecimal paidCommission; private boolean online;
}
