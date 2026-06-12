package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class DietResponse {

    private Integer id;
    private LocalDate date;

    // Thông tin hội viên
    private Integer memberId;
    private String memberName;

    // Thông tin PT lên thực đơn
    private Integer ptId;
    private String ptName;

    // Nội dung thực đơn
    private String breakfast;
    private String lunch;
    private String dinner;
}
