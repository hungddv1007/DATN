package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtMemberResponse {
    private Integer memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    
    private Integer membershipId;
    private String packageName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
