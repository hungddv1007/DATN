package datn_gym.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,
        String baseUrl,
        String nutritionModel,
        String chatModel,
        String chatComplexModel,
        Duration timeout,
        Integer chatHistoryLimit,
        Integer freeTierRequestsPerHour,
        Integer freeTierRequestsPerDay,
        Integer conversationRetentionDays) {
}
