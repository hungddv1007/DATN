package datn_gym.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlanAssignmentResponse {
    private Integer id;
    private Integer planId;
    private String planTitle;
    private Integer memberId;
    private String memberName;
    private String memberAvatar;
    private String memberGoal;
    private LocalDate startDate;
    private String status;
    private String note;
    private LocalDateTime createdAt;
}
