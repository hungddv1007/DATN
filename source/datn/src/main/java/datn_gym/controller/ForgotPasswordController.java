package datn_gym.controller;

import datn_gym.dto.request.ForgotPasswordRequest;
import datn_gym.dto.request.ResetPasswordRequest;
import datn_gym.dto.request.VerifyOtpRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    // POST /api/auth/forgot-password
    // Bước 1: Nhập email → nhận OTP qua email
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(forgotPasswordService.sendOtp(request));
    }

    // POST /api/auth/verify-otp
    // Bước 2: Xác minh OTP nhận được
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(forgotPasswordService.verifyOtp(request));
    }

    // POST /api/auth/reset-password
    // Bước 3: Đặt lại mật khẩu mới
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(forgotPasswordService.resetPassword(request));
    }
}
