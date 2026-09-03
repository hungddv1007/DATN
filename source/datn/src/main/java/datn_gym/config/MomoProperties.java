package datn_gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment.momo")
public record MomoProperties(
        Boolean enabled,
        String baseUrl,
        String partnerCode,
        String accessKey,
        String secretKey,
        String redirectUrl,
        String ipnUrl,
        String storeName,
        Integer expirationMinutes) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    public int effectiveExpirationMinutes() {
        return expirationMinutes != null && expirationMinutes > 0
                ? expirationMinutes
                : 15;
    }

    public String effectiveBaseUrl() {
        String value = require(baseUrl, "MOMO_BASE_URL");
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public String effectiveStoreName() {
        return storeName == null || storeName.isBlank() ? "GymPro" : storeName.trim();
    }

    public void requireConfigured() {
        if (!isEnabled()) {
            throw new IllegalStateException("Thanh toán MoMo hiện chưa được bật.");
        }
        require(partnerCode, "MOMO_PARTNER_CODE");
        require(accessKey, "MOMO_ACCESS_KEY");
        require(secretKey, "MOMO_SECRET_KEY");
        require(redirectUrl, "MOMO_REDIRECT_URL");
        require(ipnUrl, "MOMO_IPN_URL");
        effectiveBaseUrl();
    }

    private String require(String value, String variableName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình " + variableName + ".");
        }
        return value.trim();
    }
}
