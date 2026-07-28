package datn_gym.service;

import datn_gym.entity.OtpEntity;
import datn_gym.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public void generateAndSendOtp(String email) {
        String otp = generateAndSaveOtp(email);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean validateOtp(String email, String otp) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpirationTimeDesc(email);
        
        if (otpOpt.isEmpty()) {
            return false;
        }
        
        OtpEntity otpEntity = otpOpt.get();
        
        if (otpEntity.isUsed()) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
            return false;
        }
        
        if (otpEntity.getOtp().equals(otp)) {
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
            return true;
        }
        
        return false;
    }

    public void generateAndSendResetOtp(String email) {
        String otp = generateAndSaveOtp(email);
        emailService.sendResetPasswordEmail(email, otp);
    }

    private String generateAndSaveOtp(String email) {
        checkSpamLimit(email);
        String otp = generateOtpCode();

        OtpEntity otpEntity = OtpEntity.builder()
                .email(email)
                .otp(otp)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(otpEntity);
        return otp;
    }

    // --- Private Helpers ---

    private String generateOtpCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private void checkSpamLimit(String email) {
        LocalDateTime fiveMinsAgo = LocalDateTime.now().minusMinutes(5);
        long requestCount = otpRepository.countByEmailAndCreatedAtAfter(email, fiveMinsAgo);
        if (requestCount >= 3) {
            throw new RuntimeException("Bạn đã yêu cầu gửi mã quá nhiều lần. Vui lòng thử lại sau 5 phút.");
        }
    }
}
