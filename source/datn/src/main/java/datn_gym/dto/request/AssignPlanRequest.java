package datn_gym.dto.request;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AssignPlanRequest {
    private Integer planId;
    private List<Integer> memberIds;
    private LocalDate startDate;
    private String note;
}
