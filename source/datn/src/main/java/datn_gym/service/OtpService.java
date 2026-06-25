package datn_gym.service;

import datn_gym.entity.OtpEntity;
import datn_gym.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public void generateAndSendOtp(String email) {
        // Kiểm tra spam: tối đa 3 lần / 5 phút
        LocalDateTime fiveMinsAgo = LocalDateTime.now().minusMinutes(5);
        long requestCount = otpRepository.countByEmailAndCreatedAtAfter(email, fiveMinsAgo);
        if (requestCount >= 3) {
            throw new RuntimeException("Bạn đã yêu cầu gửi mã quá nhiều lần. Vui lòng thử lại sau 5 phút.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        
        OtpEntity otpEntity = OtpEntity.builder()
                .email(email)
                .otp(otp)
                // Hết hạn sau 5 phút
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();
                
        otpRepository.save(otpEntity);
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
}
