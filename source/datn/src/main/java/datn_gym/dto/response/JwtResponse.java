package datn_gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String hoTen;
    private String vaiTro;
    private String anhDaiDien;
}
