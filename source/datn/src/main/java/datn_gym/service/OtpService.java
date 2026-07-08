package datn_gym.service;

import datn_gym.entity.OtpEntity;
import datn_gym.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    
    // Sử dụng SecureRandom thay cho Random thường để bảo mật cao hơn
    private static final SecureRandom secureRandom = new SecureRandom();

    // =========================================================================
    // 1. CÁC HÀM SINH MÃ OTP (DÙNG CHO BƯỚC 1: GỬI OTP)
    // =========================================================================

    // Hàm sinh OTP và lưu Database (Trả về String OTP để ForgotPassword tự gửi mail)
    public String generateOtp(String email) {
        // Kiểm tra spam: tối đa 3 lần / 5 phút
        LocalDateTime fiveMinsAgo = LocalDateTime.now().minusMinutes(5);
        long requestCount = otpRepository.countByEmailAndCreatedAtAfter(email, fiveMinsAgo);
        if (requestCount >= 3) {
            throw new RuntimeException("Bạn đã yêu cầu gửi mã quá nhiều lần. Vui lòng thử lại sau 5 phút.");
        }

        // Sinh mã 6 số bảo mật
        String otp = String.format("%06d", secureRandom.nextInt(900000) + 100000);
        
        OtpEntity otpEntity = OtpEntity.builder()
                .email(email)
                .otp(otp)
                .expirationTime(LocalDateTime.now().plusMinutes(5)) // Hết hạn sau 5 phút
                .used(false)
                .build();
                
        otpRepository.save(otpEntity);
        return otp;
    }

    // Hàm tiện ích: Vừa sinh mã vừa gửi mail (AuthService đăng ký đang dùng)
    public void generateAndSendOtp(String email) {
        String otp = generateOtp(email);
        emailService.sendOtpEmail(email, otp);
    }

    // =========================================================================
    // 2. CÁC HÀM XÁC THỰC MÃ OTP
    // =========================================================================

    // Validate cho luồng Đăng ký (Kiểm tra đúng -> Đánh dấu USED luôn)
    public boolean validateOtp(String email, String otp) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpirationTimeDesc(email);
        
        if (otpOpt.isEmpty()) return false;
        
        OtpEntity otpEntity = otpOpt.get();
        if (otpEntity.isUsed() || LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
            return false;
        }
        
        if (otpEntity.getOtp().equals(otp)) {
            otpEntity.setUsed(true); // Đánh dấu đã dùng
            otpRepository.save(otpEntity);
            return true;
        }
        return false;
    }

    // Validate cho luồng Quên mật khẩu Bước 2 (Chỉ kiểm tra, KHÔNG đánh dấu USED)
    public boolean isOtpValid(String email, String otp) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpirationTimeDesc(email);
        
        if (otpOpt.isEmpty()) return false;
        
        OtpEntity otpEntity = otpOpt.get();
        if (otpEntity.isUsed() || LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
            return false;
        }
        
        return otpEntity.getOtp().equals(otp);
    }

    // Alias cho isOtpValid (phòng khi bạn dùng tên hàm verifyOtp)
    public boolean verifyOtp(String email, String otp) {
        return isOtpValid(email, otp);
    }

    // =========================================================================
    // 3. CÁC HÀM XỬ LÝ DỌN DẸP
    // =========================================================================

    // Xóa/Vô hiệu hóa OTP sau khi đã Reset Mật khẩu thành công (Luồng Quên MK Bước 3)
    public void clearOtp(String email) {
        otpRepository.findTopByEmailOrderByExpirationTimeDesc(email).ifPresent(otpEntity -> {
            otpEntity.setUsed(true); // Vô hiệu hóa
            otpRepository.save(otpEntity);
        });
    }
}