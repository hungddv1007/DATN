package datn_gym.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác thực đăng ký tài khoản GymPro");
        message.setText("Mã OTP xác thực của bạn là: " + otp + "\n\nMã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với người khác.");
        mailSender.send(message);
    }
}
