package datn_gym.service;

import datn_gym.dto.request.LoginRequest;
import datn_gym.dto.request.RegisterRequest;
import datn_gym.dto.response.JwtResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.entity.NguoiDung;
import datn_gym.entity.VaiTro;
import datn_gym.repository.NguoiDungRepository;
import datn_gym.repository.VaiTroRepository;
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
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    // Dang nhap
    public JwtResponse login(LoginRequest request) {
        // Xac thuc email + mat khau
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getMatKhau()
                )
        );

        // Tao JWT token
        String token = tokenProvider.generateToken(authentication);

        // Lay thong tin user
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(nguoiDung.getId())
                .email(nguoiDung.getEmail())
                .hoTen(nguoiDung.getHoTen())
                .vaiTro(nguoiDung.getVaiTro().getTen())
                .anhDaiDien(nguoiDung.getAnhDaiDien())
                .build();
    }

    // Dang ky (mac dinh role = MEMBER)
    public MessageResponse register(RegisterRequest request) {
        // Kiem tra email da ton tai chua
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Lay role MEMBER
        VaiTro vaiTroMember = vaiTroRepository.findByTen("MEMBER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò MEMBER"));

        // Tao user moi
        NguoiDung nguoiDung = NguoiDung.builder()
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .soDienThoai(request.getSoDienThoai())
                .vaiTro(vaiTroMember)
                .trangThai(true)
                .build();

        nguoiDungRepository.save(nguoiDung);

        return new MessageResponse("Đăng ký thành công!");
    }
}
