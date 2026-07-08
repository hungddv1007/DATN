package datn_gym.service;

import datn_gym.dto.request.ForgotPasswordRequest;
import datn_gym.dto.request.ResetPasswordRequest;
import datn_gym.dto.request.VerifyOtpRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.entity.User;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ================================================================
    // BƯỚC 1: Nhập email → Gửi OTP
    // ================================================================
    public MessageResponse sendOtp(ForgotPasswordRequest request) {
        // Kiểm tra email có tồn tại không
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản với email này"));

        // Kiểm tra tài khoản có bị khóa không
        if (Boolean.FALSE.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị khóa, không thể đặt lại mật khẩu");
        }

        // Tạo OTP và lưu vào memory
        String otp = otpService.generateOtp(request.getEmail());

        // Gửi email bất đồng bộ (@Async) — không block response
        emailService.sendOtpEmail(request.getEmail(), otp);

        return new MessageResponse(
                "Mã OTP đã được gửi đến email " + request.getEmail()
                + ". Vui lòng kiểm tra hộp thư (kể cả spam).");
    }

    // ================================================================
    // BƯỚC 2: Xác minh OTP
    // ================================================================
    public MessageResponse verifyOtp(VerifyOtpRequest request) {
        // Kiểm tra email có tồn tại không (tránh verify OTP cho email giả)
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thấy tài khoản với email này");
        }

        if (!otpService.isOtpValid(request.getEmail(), request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mã OTP không đúng hoặc đã hết hạn");
        }

        // KHÔNG xóa OTP ở bước này — giữ lại để xác minh lần nữa ở bước 3
        // Tránh trường hợp FE gọi verifyOtp trước, OTP bị xóa, rồi gọi reset thì fail

        return new MessageResponse("Xác minh OTP thành công. Vui lòng đặt mật khẩu mới.");
    }

    // ================================================================
    // BƯỚC 3: Đặt lại mật khẩu mới
    // ================================================================
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra email tồn tại
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản với email này"));

        // Kiểm tra tài khoản không bị khóa
        if (Boolean.FALSE.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị khóa, không thể đặt lại mật khẩu");
        }

        // Xác minh OTP lần cuối trước khi đổi mật khẩu
        // (đảm bảo người dùng không bỏ qua bước 2)
        if (!otpService.isOtpValid(request.getEmail(), request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mã OTP không đúng hoặc đã hết hạn. Vui lòng gửi lại OTP.");
        }

        // Đổi mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi đổi mật khẩu thành công — tránh dùng lại
        otpService.clearOtp(request.getEmail());

        return new MessageResponse("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
    }
}
