package datn_gym.service;

import datn_gym.entity.OtpEntity;
import datn_gym.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MAX_REQUESTS_PER_FIVE_MINUTES = 3;

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    /**
     * Tạo OTP và trả về mã cho các luồng cần tự xử lý email.
     */
    public String generateOtp(String email) {
        return generateAndSaveOtp(email);
    }

    /**
     * Tạo và gửi OTP đăng ký tài khoản.
     */
    public void generateAndSendOtp(String email) {
        String otp = generateAndSaveOtp(email);
        emailService.sendOtpEmail(email, otp);
    }

    /**
     * Tạo và gửi OTP đặt lại mật khẩu.
     */
    public void generateAndSendResetOtp(String email) {
        String otp = generateAndSaveOtp(email);
        emailService.sendResetPasswordEmail(email, otp);
    }

    /**
     * Kiểm tra OTP và vô hiệu hóa ngay khi xác thực thành công.
     * Dùng cho đăng ký hoặc bước reset mật khẩu cuối cùng.
     */
    @Transactional
    public boolean validateOtp(String email, String otp) {
        return validateOtpInternal(email, otp, true);
    }

    /**
     * Chỉ kiểm tra OTP, chưa vô hiệu hóa khi thành công.
     * Dùng cho bước xác minh OTP trước khi hiển thị form đổi mật khẩu.
     */
    @Transactional
    public boolean isOtpValid(String email, String otp) {
        return validateOtpInternal(email, otp, false);
    }

    /**
     * Alias tương thích với những nơi đang gọi verifyOtp().
     */
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        return validateOtpInternal(email, otp, false);
    }

    /**
     * Vô hiệu hóa OTP sau khi đổi mật khẩu thành công.
     */
    @Transactional
    public void clearOtp(String email) {
        otpRepository
                .findTopByEmailOrderByExpirationTimeDesc(email)
                .ifPresent(otpEntity -> {
                    otpEntity.setUsed(true);
                    otpRepository.save(otpEntity);
                });
    }

    private boolean validateOtpInternal(
            String email,
            String otp,
            boolean consumeOnSuccess) {

        Optional<OtpEntity> otpOptional =
                otpRepository.findTopByEmailOrderByExpirationTimeDesc(email);

        if (otpOptional.isEmpty()) {
            return false;
        }

        OtpEntity otpEntity = otpOptional.get();

        if (otpEntity.isUsed()) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
            return false;
        }

        if (!otpEntity.getOtp().equals(otp)) {
            int failedAttempts = otpEntity.getFailedAttempts() + 1;
            otpEntity.setFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                otpEntity.setUsed(true);
            }

            otpRepository.save(otpEntity);
            return false;
        }

        if (consumeOnSuccess) {
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
        }

        return true;
    }

    private String generateAndSaveOtp(String email) {
        checkSpamLimit(email);

        String otp = generateOtpCode();

        OtpEntity otpEntity = OtpEntity.builder()
                .email(email)
                .otp(otp)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .failedAttempts(0)
                .build();

        otpRepository.save(otpEntity);
        return otp;
    }

    private String generateOtpCode() {
        return String.format(
                "%06d",
                SECURE_RANDOM.nextInt(1_000_000)
        );
    }

    private void checkSpamLimit(String email) {
        LocalDateTime fiveMinutesAgo =
                LocalDateTime.now().minusMinutes(5);

        long requestCount =
                otpRepository.countByEmailAndCreatedAtAfter(
                        email,
                        fiveMinutesAgo
                );

        if (requestCount >= MAX_REQUESTS_PER_FIVE_MINUTES) {
            throw new RuntimeException(
                    "Bạn đã yêu cầu gửi mã quá nhiều lần. "
                    + "Vui lòng thử lại sau 5 phút."
            );
        }
    }
}