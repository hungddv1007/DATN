package datn_gym.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiChatServiceTest {

    private final AiChatService service = new AiChatService(
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    @Test
    void routesAccountFactsToDeterministicBackendResponse() {
        assertThat(service.isDeterministicAccountQuery(
                "Cho tôi hỏi trạng thái hiện tại của tài khoản tôi"))
                .isTrue();
        assertThat(service.isDeterministicAccountQuery(
                "Gói tập của tôi còn bao lâu?"))
                .isTrue();
        assertThat(service.isDeterministicAccountQuery(
                "Gói VIP hết hạn khi nào?"))
                .isTrue();
    }

    @Test
    void keepsCoachingQuestionsOnGeminiRoute() {
        assertThat(service.isDeterministicAccountQuery(
                "Tôi nên ăn gì trước buổi tập?"))
                .isFalse();
        assertThat(service.isDeterministicAccountQuery(
                "Hãy giải thích cách squat đúng kỹ thuật"))
                .isFalse();
    }
}
