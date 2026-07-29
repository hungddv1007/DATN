package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateScheduleRequest {

    @NotNull(message = "Vui lòng chọn ngày")
    private LocalDate scheduleDate;

    @NotNull(message = "Vui lòng chọn giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng chọn giờ kết thúc")
    private LocalTime endTime;

    @Size(max = 200, message = "Ghi chú không vượt quá 200 ký tự")
    private String exerciseNote;

    // Gửi thông báo cho học viên
    private boolean sendNotification;
}
