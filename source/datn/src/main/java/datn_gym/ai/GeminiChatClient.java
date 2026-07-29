package datn_gym.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.config.GeminiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class GeminiChatClient implements AiChatClient {

    private static final ParameterizedTypeReference<ServerSentEvent<String>> SSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Duration timeout;

    public GeminiChatClient(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            GeminiProperties properties) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình GEMINI_API_KEY.");
        }
        if (properties.baseUrl() == null || properties.baseUrl().isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình GEMINI_BASE_URL.");
        }
        this.webClient = webClientBuilder
                .baseUrl(properties.baseUrl().trim())
                .defaultHeader("x-goog-api-key", properties.apiKey())
                .build();
        this.objectMapper = objectMapper;
        this.timeout = properties.timeout() != null
                ? properties.timeout()
                : Duration.ofSeconds(30);
    }

    @Override
    public Flux<AiChatStreamEvent> streamChat(
            String model,
            String systemInstruction,
            String prompt,
            boolean deepAnalysis) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "input", prompt,
                "system_instruction", systemInstruction,
                "stream", true,
                "store", false,
                "generation_config", Map.of(
                        "thinking_level", deepAnalysis ? "medium" : "minimal",
                        "max_output_tokens", deepAnalysis ? 2048 : 1024
                )
        );

        return webClient.post()
                .uri("/v1beta/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(SSE_TYPE)
                                .flatMapIterable(this::parseEvent);
                    }
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMapMany(body -> Flux.error(
                                    mapHttpError(response.statusCode().value())));
                })
                .timeout(timeout)
                .onErrorMap(throwable -> {
                    if (throwable instanceof ResponseStatusException) {
                        return throwable;
                    }
                    log.warn("Không thể stream phản hồi Gemini Interactions API: {}",
                            throwable.getMessage());
                    return new ResponseStatusException(
                            HttpStatus.BAD_GATEWAY,
                            "Không thể nhận phản hồi từ dịch vụ AI.");
                });
    }

    private Iterable<AiChatStreamEvent> parseEvent(ServerSentEvent<String> event) {
        String data = event.data();
        if (data == null || data.isBlank() || "[DONE]".equals(data)) {
            return java.util.List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(data);
            String eventType = root.path("event_type").asText();
            if ("step.delta".equals(eventType)
                    && "text".equals(root.path("delta").path("type").asText())) {
                String text = root.path("delta").path("text").asText("");
                return text.isEmpty()
                        ? java.util.List.of()
                        : java.util.List.of(AiChatStreamEvent.text(text));
            }
            if ("interaction.completed".equals(eventType)) {
                JsonNode usage = root.path("interaction").path("usage");
                return java.util.List.of(AiChatStreamEvent.completed(new AiUsage(
                        usage.path("total_input_tokens").asInt(0),
                        usage.path("total_output_tokens").asInt(0),
                        usage.path("total_tokens").asInt(0))));
            }
            if ("error".equals(eventType)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Dịch vụ AI không thể hoàn tất câu trả lời.");
            }
            return java.util.List.of();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("Bỏ qua sự kiện Gemini không nhận diện được", ex);
            return java.util.List.of();
        }
    }

    private ResponseStatusException mapHttpError(int status) {
        if (status == 429) {
            return new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Đã chạm giới hạn Gemini Free Tier. Vui lòng thử lại sau.");
        }
        if (status == 400 || status == 401 || status == 403) {
            return new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Dịch vụ AI chưa được cấu hình đúng.");
        }
        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Dịch vụ AI tạm thời không khả dụng.");
    }
}
