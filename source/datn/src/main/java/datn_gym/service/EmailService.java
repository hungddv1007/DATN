package datn_gym.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // @Async: Gửi email bất đồng bộ - Giúp API trả về response ngay lập tức
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("GymPro — Mã OTP Xác Thực");
            
            // Sử dụng HTML Template
            helper.setText(buildEmailContent(otp), true);

            mailSender.send(message);
        } catch (Exception e) {
            // Chỉ log lỗi chứ không làm sập API vì đang chạy @Async
            System.err.println("Lỗi gửi email OTP tới " + toEmail + ": " + e.getMessage());
        }
    }

    // Template HTML dùng chung (Đăng ký / Quên mật khẩu đều hợp lý)
    private String buildEmailContent(String otp) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #FF6B00; margin-bottom: 8px;">GymPro</h2>
                <p style="color: #333; font-size: 15px;">Bạn đã yêu cầu một mã xác thực. Vui lòng sử dụng mã OTP dưới đây để tiếp tục:</p>
                <div style="background: #f5f5f5; padding: 16px; text-align: center; border-radius: 6px; margin: 20px 0;">
                    <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #FF6B00;">%s</span>
                </div>
                <p style="color: #666; font-size: 13px;">Mã OTP có hiệu lực trong <strong>5 phút</strong>.</p>
                <p style="color: #666; font-size: 13px; margin-top: 24px; border-top: 1px solid #eee; padding-top: 12px;">
                    Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email. Tuyệt đối không chia sẻ mã này cho bất kỳ ai.<br><br>
                    Trân trọng,<br><strong>Đội ngũ GymPro</strong>
                </p>
            </div>
        """.formatted(otp);
    }
}