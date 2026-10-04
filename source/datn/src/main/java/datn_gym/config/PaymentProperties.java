package datn_gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment")
public record PaymentProperties(
        String bankName,
        String bankAccountNumber,
        String bankAccountHolder,
        String transferPrefix,
        Integer pendingExpirationHours) {

    public int effectivePendingExpirationHours() {
        return pendingExpirationHours != null && pendingExpirationHours > 0
                ? pendingExpirationHours
                : 24;
    }
}
