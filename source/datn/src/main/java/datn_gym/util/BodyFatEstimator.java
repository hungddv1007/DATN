package datn_gym.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

/**
 * Ước tính tỷ lệ mỡ người trưởng thành theo công thức Deurenberg (1991):
 * BF% = 1.20 × BMI + 0.23 × tuổi − 10.8 × giới tính − 5.4,
 * trong đó giới tính sinh học nam = 1, nữ = 0.
 */
public final class BodyFatEstimator {

    private static final int MINIMUM_AGE = 18;

    private BodyFatEstimator() {
    }

    public static Optional<BigDecimal> estimateAdult(
            BigDecimal heightCm,
            BigDecimal weightKg,
            LocalDate dateOfBirth,
            String biologicalSex,
            LocalDate referenceDate) {
        if (heightCm == null || weightKg == null || dateOfBirth == null
                || biologicalSex == null || referenceDate == null) {
            return Optional.empty();
        }

        int age = ageOn(dateOfBirth, referenceDate);
        if (age < MINIMUM_AGE
                || !("MALE".equals(biologicalSex) || "FEMALE".equals(biologicalSex))) {
            return Optional.empty();
        }

        double heightMeters = heightCm.doubleValue() / 100.0;
        if (heightMeters <= 0 || weightKg.signum() <= 0) {
            return Optional.empty();
        }

        double bmi = weightKg.doubleValue() / (heightMeters * heightMeters);
        int sexCoefficient = "MALE".equals(biologicalSex) ? 1 : 0;
        double estimatedPercentage = 1.20 * bmi
                + 0.23 * age
                - 10.8 * sexCoefficient
                - 5.4;

        if (!Double.isFinite(estimatedPercentage)
                || estimatedPercentage < 0
                || estimatedPercentage > 100) {
            return Optional.empty();
        }

        return Optional.of(BigDecimal.valueOf(estimatedPercentage)
                .setScale(1, RoundingMode.HALF_UP));
    }

    public static int ageOn(LocalDate dateOfBirth, LocalDate referenceDate) {
        if (dateOfBirth == null || referenceDate == null
                || dateOfBirth.isAfter(referenceDate)) {
            return -1;
        }
        return Period.between(dateOfBirth, referenceDate).getYears();
    }
}
