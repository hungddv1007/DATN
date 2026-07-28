package datn_gym.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class GeminiClient implements AiClient {

    private final WebClient webClient;
    private final Duration timeout;

    public GeminiClient(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api.url}") String apiUrl,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.timeout:30s}") Duration timeout) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình GEMINI_API_KEY.");
        }
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                // Không đặt API key trên query string để tránh lộ qua access log.
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
        this.timeout = timeout;
    }

    @Override
    public String generateJson(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "maxOutputTokens", 8192,
                        "responseMimeType", "application/json"
                )
        );

        try {
            Map<?, ?> response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(timeout)
                    .block(timeout.plusSeconds(1));
            return extractText(response);
        } catch (WebClientResponseException ex) {
            log.warn("Gemini trả về HTTP {}", ex.getStatusCode().value());
            if (ex.getStatusCode().value() == 429) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Dịch vụ AI đang quá tải. Vui lòng thử lại sau.");
            }
            if (ex.getStatusCode().value() == 400
                    || ex.getStatusCode().value() == 401
                    || ex.getStatusCode().value() == 403) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Dịch vụ AI chưa được cấu hình đúng.");
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể nhận phản hồi từ dịch vụ AI.");
        } catch (RuntimeException ex) {
            if (hasCause(ex, TimeoutException.class)) {
                throw new ResponseStatusException(
                        HttpStatus.GATEWAY_TIMEOUT,
                        "Dịch vụ AI phản hồi quá lâu. Vui lòng thử lại.");
            }
            if (ex instanceof ResponseStatusException responseStatusException) {
                throw responseStatusException;
            }
            log.error("Không thể gọi Gemini", ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể kết nối tới dịch vụ AI.");
        }
    }

    private String extractText(Map<?, ?> response) {
        try {
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
            String text = (String) firstPart.get("text");
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("AI trả về nội dung rỗng.");
            }
            return text;
        } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
            throw new IllegalStateException("Cấu trúc phản hồi AI không hợp lệ.", ex);
        }
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> causeType) {
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
