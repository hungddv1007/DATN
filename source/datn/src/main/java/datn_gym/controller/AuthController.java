package datn_gym.controller;

import datn_gym.dto.request.ForgotPasswordRequest;
import datn_gym.dto.request.LoginRequest;
import datn_gym.dto.request.RegisterRequest;
import datn_gym.dto.request.ResetPasswordRequest;
import datn_gym.dto.request.SendOtpRequest;
import datn_gym.dto.response.JwtResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.google.client-id}")
    private String googleClientId;

    // GET /api/auth/google/client-id
    @GetMapping("/google/client-id")
    public ResponseEntity<Map<String, String>> getGoogleClientId() {
        return ResponseEntity.ok(Map.of("clientId", googleClientId));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/send-otp
    @PostMapping("/send-otp")
    public ResponseEntity<MessageResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        MessageResponse response = authService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/google
    @PostMapping("/google")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Token Google không được để trống!");
        }
        JwtResponse response = authService.loginWithGoogle(idToken);
        return ResponseEntity.ok(response);
    }

    // GET /api/auth/test - API test kiểm tra server chạy
    @GetMapping("/test")
    public ResponseEntity<MessageResponse> test() {
        return ResponseEntity.ok(new MessageResponse("GymPro API is running!"));
    }

    // POST /api/auth/forgot-password — Gửi OTP đặt lại mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.forgotPassword(request.getEmail().trim());
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/reset-password — Xác thực OTP + Đổi mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(
                request.getEmail(), request.getOtp(),
                request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(response);
    }
}