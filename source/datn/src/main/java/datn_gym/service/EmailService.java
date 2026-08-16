package datn_gym.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        sendEmail(
                toEmail,
                "GymPro — Mã OTP xác thực tài khoản",
                "Bạn đã yêu cầu mã xác thực tài khoản GymPro.",
                otp
        );
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String otp) {
        sendEmail(
                toEmail,
                "GymPro — Đặt lại mật khẩu",
                "Bạn đã yêu cầu đặt lại mật khẩu tài khoản GymPro.",
                otp
        );
    }

    @Async
    public void sendMembershipConfirmedEmail(
            String recipientEmail,
            String customerName,
            Integer transactionId,
            String packageName,
            String transactionType,
            BigDecimal amount,
            Integer termsVersion) {
        String content = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:auto;padding:24px;border:1px solid #e5e7eb;border-radius:12px">
              <h2 style="color:#ff6b00">GymPro — Xác nhận giao dịch</h2>
              <p>Xin chào <strong>%s</strong>, giao dịch của bạn đã được xác nhận thành công.</p>
              <table style="width:100%%;border-collapse:collapse;background:#f8fafc">
                <tr><td style="padding:10px">Mã giao dịch</td><td style="padding:10px"><strong>#%s</strong></td></tr>
                <tr><td style="padding:10px">Gói tập</td><td style="padding:10px"><strong>%s</strong></td></tr>
                <tr><td style="padding:10px">Loại giao dịch</td><td style="padding:10px">%s</td></tr>
                <tr><td style="padding:10px">Số tiền</td><td style="padding:10px"><strong>%,.0f ₫</strong></td></tr>
                <tr><td style="padding:10px">Điều khoản đã đồng ý</td><td style="padding:10px">Phiên bản %s</td></tr>
              </table>
              <p style="font-size:13px;color:#64748b">Thông tin giao dịch và phiên bản điều khoản được lưu tại GymPro để đối chiếu khi cần.</p>
            </div>
            """.formatted(
                escape(customerName),
                transactionId, escape(packageName), escape(transactionType),
                amount, termsVersion == null ? "không áp dụng" : termsVersion);
        sendHtml(recipientEmail,
                "GymPro — Giao dịch #" + transactionId + " đã được xác nhận", content);
    }

    private void sendEmail(
            String toEmail,
            String subject,
            String description,
            String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildEmailContent(description, otp), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error(
                    "Lỗi gửi email OTP tới {}: {}",
                    toEmail,
                    e.getMessage()
            );
        }
    }

    private void sendHtml(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi email giao dịch tới {}: {}", toEmail, e.getMessage());
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String buildEmailContent(String description, String otp) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 480px;
                        margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0;
                        border-radius: 8px;">
                <h2 style="color: #FF6B00; margin-bottom: 8px;">GymPro</h2>

                <p style="color: #333; font-size: 15px;">%s</p>

                <p style="color: #333; font-size: 15px;">
                    Vui lòng sử dụng mã OTP dưới đây để tiếp tục:
                </p>

                <div style="background: #f5f5f5; padding: 16px;
                            text-align: center; border-radius: 6px;
                            margin: 20px 0;">
                    <span style="font-size: 36px; font-weight: bold;
                                 letter-spacing: 8px; color: #FF6B00;">
                        %s
                    </span>
                </div>

                <p style="color: #666; font-size: 13px;">
                    Mã OTP có hiệu lực trong <strong>5 phút</strong>.
                </p>

                <p style="color: #666; font-size: 13px; margin-top: 24px;
                          border-top: 1px solid #eee; padding-top: 12px;">
                    Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
                    Tuyệt đối không chia sẻ mã OTP cho bất kỳ ai.
                    <br><br>
                    Trân trọng,<br>
                    <strong>Đội ngũ GymPro</strong>
                </p>
            </div>
            """.formatted(description, otp);
    }
}
