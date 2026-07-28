package datn_gym.ai;

/**
 * Cổng dùng chung cho các tính năng AI. Chatbot sau này phụ thuộc interface
 * này thay vì gọi trực tiếp SDK/API của một nhà cung cấp.
 */
public interface AiClient {

    String generateJson(String prompt);
}
