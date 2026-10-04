package datn_gym.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferVerificationRequest {
    @NotBlank @Email private String recipientEmail;
    private String password;
    private String googleIdToken;
}
