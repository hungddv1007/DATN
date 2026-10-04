package datn_gym.service;

import datn_gym.config.GeminiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiRateLimitServiceTest {

    @Test
    void blocksRequestsAboveHourlyFreeTierLimit() {
        AiRateLimitService service = new AiRateLimitService(properties(2, 20));

        service.checkAndRecord(7);
        service.checkAndRecord(7);

        assertThatThrownBy(() -> service.checkAndRecord(7))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode())
                                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS));
    }

    @Test
    void keepsLimitsSeparateForEachMember() {
        AiRateLimitService service = new AiRateLimitService(properties(1, 20));

        service.checkAndRecord(7);
        service.checkAndRecord(8);

        assertThatThrownBy(() -> service.checkAndRecord(7))
                .isInstanceOf(ResponseStatusException.class);
    }

    private GeminiProperties properties(int hourly, int daily) {
        return new GeminiProperties(
                "key",
                "https://example.test",
                "nutrition",
                "chat",
                "complex",
                Duration.ofSeconds(30),
                12,
                hourly,
                daily,
                90);
    }
}
