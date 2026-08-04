package datn_gym.service;

import datn_gym.dto.request.ForgotPasswordRequest;
import datn_gym.dto.request.VerifyOtpRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.entity.User;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ForgotPasswordService service;

    @BeforeEach
    void setUp() {
        service = new ForgotPasswordService(
                userRepository,
                otpService,
                emailService,
                passwordEncoder);
    }

    @Test
    void rejectsIncorrectOtpBeforePasswordStep() {
        VerifyOtpRequest request = request("member@gym.local", "111111");
        when(otpService.isOtpValid(request.getEmail(), request.getOtp()))
                .thenReturn(false);

        assertThatThrownBy(() -> service.verifyOtp(request))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> {
                            assertThat(exception.getStatusCode())
                                    .isEqualTo(HttpStatus.BAD_REQUEST);
                            assertThat(exception.getReason())
                                    .isEqualTo("Mã OTP không đúng hoặc đã hết hạn");
                        });

        verify(otpService).isOtpValid(request.getEmail(), request.getOtp());
    }

    @Test
    void acceptsCorrectOtpAndAllowsPasswordStep() {
        VerifyOtpRequest request = request("member@gym.local", "567128");
        when(otpService.isOtpValid(request.getEmail(), request.getOtp()))
                .thenReturn(true);

        MessageResponse response = service.verifyOtp(request);

        assertThat(response.getMessage())
                .isEqualTo("Xác minh OTP thành công. Vui lòng đặt mật khẩu mới.");
    }

    @Test
    void forgotPasswordDoesNotRevealUnknownEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("Unknown@Gym.Local");
        when(userRepository.findByEmail("unknown@gym.local")).thenReturn(Optional.empty());

        MessageResponse response = service.sendOtp(request);

        assertThat(response.getMessage()).isNotBlank();
        verifyNoInteractions(otpService, emailService);
    }

    @Test
    void forgotPasswordSendsResetOtpForActiveAccount() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("Member@Gym.Local");
        User active = User.builder().email("member@gym.local").status(true).build();
        when(userRepository.findByEmail("member@gym.local")).thenReturn(Optional.of(active));
        when(otpService.generateOtp("member@gym.local")).thenReturn("567128");

        MessageResponse response = service.sendOtp(request);

        assertThat(response.getMessage()).isNotBlank();
        verify(emailService).sendResetPasswordEmail("member@gym.local", "567128");
    }

    private VerifyOtpRequest request(String email, String otp) {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail(email);
        request.setOtp(otp);
        return request;
    }
}
