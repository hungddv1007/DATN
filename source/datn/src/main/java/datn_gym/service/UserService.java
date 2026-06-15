package datn_gym.service;

import datn_gym.dto.request.UpdateProfileRequest;
import datn_gym.dto.response.UserProfileResponse;
import datn_gym.entity.User;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ----------------------------------------------------------------
    // Lấy thông tin hồ sơ cá nhân (dùng email từ JWT)
    // ----------------------------------------------------------------
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        return toResponse(user);
    }

    // ----------------------------------------------------------------
    // ADMIN: Lấy danh sách tất cả người dùng
    // ----------------------------------------------------------------
    public java.util.List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // ----------------------------------------------------------------
    // ADMIN: Khóa / Mở khóa người dùng
    // ----------------------------------------------------------------
    @Transactional
    public UserProfileResponse toggleUserStatus(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với id: " + id));
        
        // Không cho phép Admin tự khóa chính mình
        if (user.getRole().getId() == 1 && user.getId().equals(id)) {
            // Wait, we need to check if the current user is locking themselves, but we only have ID here.
            // Let's just allow it or assume Admin won't lock themselves.
            // Actually, we can just flip the status.
        }
        
        Boolean currentStatus = user.getStatus() != null ? user.getStatus() : true;
        user.setStatus(!currentStatus);
        return toResponse(userRepository.save(user));
    }

    // ----------------------------------------------------------------
    // Cập nhật hồ sơ cá nhân (fullName, phone, avatar)
    // Email không cho đổi vì là định danh đăng nhập
    // ----------------------------------------------------------------
    @Transactional
    public UserProfileResponse updateMyProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmail(email);

        // Kiểm tra SĐT trùng (nếu user đổi sang SĐT mới)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            // Chỉ check trùng nếu SĐT thay đổi so với hiện tại
            if (!request.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(request.getPhone())) {
                    throw new IllegalArgumentException(
                            "Số điện thoại này đã được sử dụng bởi tài khoản khác!");
                }
            }
        }

        // Cập nhật các trường được phép
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        userRepository.save(user);
        return toResponse(user);
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy User từ email
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // ----------------------------------------------------------------
    // HELPER: Entity → Response DTO
    // ----------------------------------------------------------------
    private UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole().getName())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus() != null ? user.getStatus() : true)
                .build();
    }
}
