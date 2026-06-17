package datn_gym.dto.response;

import lombok.Builder;
import lombok.Data;

// Response thống kê nhanh — dùng kèm khi HV/PT xem tổng quan lộ trình
@Data
@Builder
public class AttendanceSummaryResponse {
    private Integer routeId;
    private String routeName;
    private Long totalSessions;    // Tổng buổi tập (không tính rest day)
    private Long presentSessions;  // Số buổi có mặt
    private Long absentSessions;   // Số buổi vắng
    private Double attendanceRate; // Tỉ lệ % có mặt
}
