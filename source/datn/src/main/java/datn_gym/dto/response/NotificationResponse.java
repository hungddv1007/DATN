package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Integer id;

    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Người gửi — null nếu là thông báo hệ thống
    private Integer senderId;
    private String senderName;
}
