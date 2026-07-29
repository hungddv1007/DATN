package datn_gym.ai;

import java.util.Map;

/**
 * Cổng dùng chung cho các tác vụ AI trả về dữ liệu có cấu trúc.
 */
public interface AiClient {

    String generateStructuredJson(String prompt, Map<String, Object> jsonSchema);
}
