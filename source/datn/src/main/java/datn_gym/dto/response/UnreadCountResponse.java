package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

// Response gọn — dùng cho badge số thông báo chưa đọc trên FE
@Data
@Builder
public class UnreadCountResponse {
    private Long unreadCount;
}
