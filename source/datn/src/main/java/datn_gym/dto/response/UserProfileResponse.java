package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String role;
    private LocalDateTime createdAt;
    private Boolean status;
}
