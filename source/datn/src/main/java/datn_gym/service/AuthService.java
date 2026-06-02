package datn_gym.service;

import datn_gym.dto.request.LoginRequest;
import datn_gym.dto.request.RegisterRequest;
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

    // Đăng nhập
    public JwtResponse login(LoginRequest request) {
        // Xác thực email + mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Tạo JWT token
        String token = tokenProvider.generateToken(authentication);

        // Lấy thông tin user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

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

    // Đăng ký (mặc định role = MEMBER)
    public MessageResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Lấy role MEMBER
        Role roleMember = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò MEMBER"));

        // Tạo user mới
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(roleMember)
                .status(true)
                .build();

        userRepository.save(user);

        return new MessageResponse("Đăng ký thành công!");
    }
}