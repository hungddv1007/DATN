package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class DietResponse {

    private Integer id;

    // Loại ngày & ngày cụ thể
    private String dayType;           // TRAINING_DAY, REST_DAY, SPECIFIC_DATE
    private LocalDate dietDate;       // NULL nếu là mẫu
    private Boolean isTrainingDay;    // Tính toán bằng auto-mapping (dùng cho Member view)

    private String title;

    // Thông tin hội viên
    private Integer memberId;
    private String memberName;

    // Thông tin PT
    private Integer ptId;
    private String ptName;

    // Nội dung thực đơn (5 bữa)
    private String breakfast;
    private String snackMorning;
    private String lunch;
    private String snackAfternoon;
    private String dinner;

    // Chỉ số dinh dưỡng
    private Integer calories;
    private Integer proteinG;
    private Integer carbsG;
    private Integer fatG;

    private String note;
}
