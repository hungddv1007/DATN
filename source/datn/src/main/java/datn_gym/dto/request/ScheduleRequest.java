package datn_gym.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleRequest {

    private Integer memberId;
    private List<SlotItem> slots;

    @Data
    public static class SlotItem {
        private Integer dayOfWeek;  // 0-5 (T2-T7)
        private Integer slotIndex;  // 0-7 (8 khung giờ)
        private String exerciseNote; // "Ngực", "Chân + Core"
    }
}
