package datn_gym.security;

import datn_gym.entity.NguoiDung;
import datn_gym.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với email: " + email));

        // Kiem tra tai khoan co bi khoa khong
        if (!nguoiDung.getTrangThai()) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }

        return new User(
                nguoiDung.getEmail(),
                nguoiDung.getMatKhau(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().getTen())
                )
        );
    }
}
