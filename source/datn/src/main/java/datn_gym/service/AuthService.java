package datn_gym.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;

    @Value("${app.google.client-id}")
    private String googleClientId;

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

    // Đăng nhập / Đăng ký bằng Google
    public JwtResponse loginWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Token Google không hợp lệ!");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");
            String avatar = (String) payload.get("picture");

            // Tìm user đã tồn tại
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();

                // Kiểm tra tài khoản bị khóa
                if (!user.getStatus()) {
                    throw new IllegalArgumentException("Tài khoản đã bị khóa!");
                }

                // Cập nhật avatar từ Google nếu user chưa có avatar
                if (user.getAvatar() == null && avatar != null) {
                    user.setAvatar(avatar);
                    userRepository.save(user);
                }
            } else {
                // Tạo tài khoản mới từ Google
                Role roleMember = roleRepository.findByName("MEMBER")
                        .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy vai trò MEMBER"));

                user = User.builder()
                        .email(email)
                        .password(null)
                        .fullName(fullName != null ? fullName : email)
                        .avatar(avatar)
                        .role(roleMember)
                        .status(true)
                        .provider("GOOGLE")
                        .build();

                userRepository.save(user);
            }

            // Tạo JWT token
            String token = tokenProvider.generateTokenFromEmail(email);

            return JwtResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().getName())
                    .avatar(user.getAvatar())
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
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
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }

        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được đăng ký. Vui lòng sử dụng email khác!");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng. Vui lòng nhập số khác!");
        }

        Role roleMember = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy vai trò MEMBER"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(roleMember)
                .status(true)
                .provider("LOCAL")
                .build();

        userRepository.save(user);

        return new MessageResponse("Đăng ký tài khoản thành công!");
    }
}