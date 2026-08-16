package datn_gym.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MembershipTransferServiceTest {
    @Test
    void deductionUsesTenPercentWithMinimumThreeDays() {
        assertThat(MembershipTransferService.calculateDeductionDays(20)).isEqualTo(3);
        assertThat(MembershipTransferService.calculateDeductionDays(31)).isEqualTo(4);
    }

    @Test
    void deductionIsCappedAtThirtyDays() {
        assertThat(MembershipTransferService.calculateDeductionDays(100)).isEqualTo(10);
        assertThat(MembershipTransferService.calculateDeductionDays(400)).isEqualTo(30);
    }
}
