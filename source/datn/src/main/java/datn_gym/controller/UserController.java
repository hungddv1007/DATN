package datn_gym.controller;

import datn_gym.dto.request.UpdateProfileRequest;
import datn_gym.dto.response.UserProfileResponse;
import datn_gym.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/profile — Lấy hồ sơ cá nhân (dùng JWT để xác định user)
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getMyProfile(email));
    }

    // PUT /api/users/profile — Cập nhật hồ sơ cá nhân
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateMyProfile(email, request));
    }
}
