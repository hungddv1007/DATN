package datn_gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// Bật @Async để EmailService gửi email bất đồng bộ
// Không có annotation này, @Async sẽ bị bỏ qua và gửi email đồng bộ
// (API sẽ bị chờ đến khi email gửi xong mới trả response — ảnh hưởng UX)
@Configuration
@EnableAsync
public class AsyncConfig {
}
