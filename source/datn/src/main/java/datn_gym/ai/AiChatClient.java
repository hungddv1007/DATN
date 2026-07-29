package datn_gym.ai;

import reactor.core.publisher.Flux;

public interface AiChatClient {

    Flux<AiChatStreamEvent> streamChat(
            String model,
            String systemInstruction,
            String prompt,
            boolean deepAnalysis);
}
