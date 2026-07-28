package datn_gym.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Xác thực đăng ký tài khoản GymPro");
            message.setText("Mã OTP xác thực của bạn là: " + otp + "\n\nMã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với người khác.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi OTP email tới {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Đặt lại mật khẩu GymPro");
            message.setText("Bạn đã yêu cầu đặt lại mật khẩu tại GymPro.\n\nMã OTP của bạn là: " + otp + "\n\nMã có hiệu lực trong 5 phút.\nNếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi reset password email tới {}: {}", toEmail, e.getMessage());
        }
    }
}
