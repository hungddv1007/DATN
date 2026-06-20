package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberProfileResponse {

    // Từ bảng users
    private Integer id;          // user_id
    private String fullName;
    private String email;
    private String phone;
    private String avatar;

    // Từ bảng member_profiles
    private String physicalCondition;
}
