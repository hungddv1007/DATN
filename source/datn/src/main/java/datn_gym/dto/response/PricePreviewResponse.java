package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PricePreviewResponse {
    private String currentPackageName;
    private String newPackageName;
    private BigDecimal currentDailyPrice;
    private BigDecimal newDailyPrice;
    private Integer remainingDays;
    private Integer extraDays;
    private Integer totalNewDays;

    private BigDecimal grossAmount;        // Giá gốc
    private Integer longTermDiscount;      // % chiết khấu dài hạn
    private BigDecimal afterDiscount;      // Sau chiết khấu
    private BigDecimal credit;             // Credit (proration)
    private BigDecimal finalAmount;        // Phải trả
    private Integer codeDiscount;          // % giảm thêm từ mã khuyến mãi / giới thiệu
    private String codeType;               // PROMOTION | SALE_REFERRAL | null
    private String type;                   // RENEW | UPGRADE | UPGRADE_RENEW
}
