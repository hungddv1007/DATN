package datn_gym.service;

import datn_gym.dto.request.LoginRequest;
import datn_gym.dto.request.RegisterRequest;
import datn_gym.dto.request.SendOtpRequest;
import datn_gym.dto.response.JwtResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.entity.Role;
import datn_gym.entity.User;
import datn_gym.repository.RoleRepository;
import datn_gym.repository.UserRepository;
import datn_gym.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;

    // Đăng nhập
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .avatar(user.getAvatar())
                .build();
    }

    // Gửi OTP
    public MessageResponse sendOtp(SendOtpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được đăng ký. Vui lòng sử dụng email khác!");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng. Vui lòng nhập số khác!");
        }
        otpService.generateAndSendOtp(request.getEmail());
        return new MessageResponse("Mã OTP đã được gửi đến email của bạn. Mã có hiệu lực trong 5 phút.");
    }

    // Đăng ký
    public MessageResponse register(RegisterRequest request) {
        // Lỗi 1: Mật khẩu xác nhận không khớp
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }

        // Lỗi xác thực OTP
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Lỗi 2: Email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được đăng ký. Vui lòng sử dụng email khác!");
        }

        // Lỗi 3: Số điện thoại đã tồn tại 
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng. Vui lòng nhập số khác!");
        }

        // Lỗi 4: Không tìm thấy Role MEMBER
        Role roleMember = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy vai trò MEMBER"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(roleMember)
                .status(true)
                .build();

        userRepository.save(user);

        return new MessageResponse("Đăng ký tài khoản thành công!");
    }
}