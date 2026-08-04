package datn_gym.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BodyFatEstimatorTest {

    private static final LocalDate REFERENCE_DATE = LocalDate.of(2026, 8, 4);

    @Test
    void estimatesAdultMaleBodyFatUsingDeurenbergFormula() {
        var result = BodyFatEstimator.estimateAdult(
                new BigDecimal("172"),
                new BigDecimal("70"),
                LocalDate.of(2000, 1, 1),
                "MALE",
                REFERENCE_DATE);

        assertThat(result).contains(new BigDecimal("18.2"));
    }

    @Test
    void doesNotEstimateForMinorOrMissingBiologicalSex() {
        assertThat(BodyFatEstimator.estimateAdult(
                new BigDecimal("172"),
                new BigDecimal("70"),
                LocalDate.of(2010, 1, 1),
                "FEMALE",
                REFERENCE_DATE)).isEmpty();
        assertThat(BodyFatEstimator.estimateAdult(
                new BigDecimal("172"),
                new BigDecimal("70"),
                LocalDate.of(2000, 1, 1),
                null,
                REFERENCE_DATE)).isEmpty();
    }

    @Test
    void calculatesAgeAroundBirthdayCorrectly() {
        assertThat(BodyFatEstimator.ageOn(
                LocalDate.of(2000, 1, 1), REFERENCE_DATE)).isEqualTo(26);
        assertThat(BodyFatEstimator.ageOn(
                LocalDate.of(2000, 12, 1), REFERENCE_DATE)).isEqualTo(25);
    }
}
