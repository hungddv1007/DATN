package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PtProfileResponse {

    // Từ bảng users
    private Integer id;          // user_id
    private String fullName;
    private String email;
    private String phone;
    private String avatar;

    // Từ bảng pt_profiles
    private String specialization;
    private String bio;
    private String certificates;
    private BigDecimal ratingScore;

    // Thống kê bổ sung
    private Integer totalMembers;    // Số hội viên đang được giao
    private Integer totalReviews;    // Tổng số đánh giá
}