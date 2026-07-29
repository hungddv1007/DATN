package datn_gym.service;

import datn_gym.config.GeminiProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiRateLimitService {

    private final ConcurrentHashMap<Integer, Deque<Instant>> requestsByUser =
            new ConcurrentHashMap<>();
    private final int hourlyLimit;
    private final int dailyLimit;

    public AiRateLimitService(GeminiProperties properties) {
        this.hourlyLimit = positiveOrDefault(
                properties.freeTierRequestsPerHour(), 10);
        this.dailyLimit = positiveOrDefault(
                properties.freeTierRequestsPerDay(), 50);
    }

    public void checkAndRecord(Integer userId) {
        Instant now = Instant.now();
        Deque<Instant> requests =
                requestsByUser.computeIfAbsent(userId, ignored -> new ArrayDeque<>());

        synchronized (requests) {
            Instant oneDayAgo = now.minus(Duration.ofDays(1));
            while (!requests.isEmpty() && requests.peekFirst().isBefore(oneDayAgo)) {
                requests.removeFirst();
            }

            if (requests.size() >= dailyLimit) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bạn đã dùng hết lượt AI trong ngày dành cho Free Tier.");
            }

            Instant oneHourAgo = now.minus(Duration.ofHours(1));
            long requestsInLastHour = requests.stream()
                    .filter(timestamp -> !timestamp.isBefore(oneHourAgo))
                    .count();
            if (requestsInLastHour >= hourlyLimit) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bạn đã dùng hết lượt AI trong giờ này. Vui lòng thử lại sau.");
            }

            requests.addLast(now);
        }
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }
}
