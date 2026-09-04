package datn_gym.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateScheduleRequest {

    @NotNull(message = "Vui lòng chọn học viên")
    private Integer memberId;

    @NotNull(message = "Vui lòng chọn ngày")
    private LocalDate scheduleDate;

    @NotNull(message = "Vui lòng chọn giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng chọn giờ kết thúc")
    private LocalTime endTime;

    @Size(max = 200, message = "Ghi chú không vượt quá 200 ký tự")
    private String exerciseNote;

    // Lặp lại hàng tuần
    private boolean recurring;
    @Min(value = 2, message = "Lịch lặp phải có ít nhất 2 tuần")
    @Max(value = 15, message = "Lịch lặp không được vượt quá 15 tuần")
    private Integer recurringWeeks; // 2-15, mặc định 8

    // Gửi thông báo cho học viên
    private boolean sendNotification;
}
