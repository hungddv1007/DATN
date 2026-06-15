package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BlogResponse {
    private Integer id;
    private String title;
    private String content;
    private String thumbnail;
    private String status;
    private String authorName;
    private LocalDateTime createdAt;
}
