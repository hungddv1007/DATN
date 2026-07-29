package datn_gym.ai;

public record AiChatStreamEvent(
        String text,
        AiUsage usage,
        boolean completed) {

    public static AiChatStreamEvent text(String text) {
        return new AiChatStreamEvent(text, null, false);
    }

    public static AiChatStreamEvent completed(AiUsage usage) {
        return new AiChatStreamEvent(null, usage, true);
    }
}
