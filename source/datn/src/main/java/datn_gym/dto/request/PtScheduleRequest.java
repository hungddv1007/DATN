package datn_gym.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PtScheduleRequest {
    
    @NotNull(message = "ID thành viên không được để trống")
    private Integer memberId;

    @NotNull(message = "Ngày trong tuần không được để trống")
    @Min(value = 0, message = "Ngày không hợp lệ")
    @Max(value = 6, message = "Ngày không hợp lệ")
    private Integer dayOfWeek;

    @NotBlank(message = "Slot giờ không được để trống")
    private String timeSlot;
}
