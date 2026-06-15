package datn_gym.controller;

import datn_gym.dto.response.UserProfileResponse;
import datn_gym.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // GET /api/admin/users — Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // PUT /api/admin/users/{id}/toggle-status — Khóa/Mở khóa người dùng
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<UserProfileResponse> toggleUserStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }
}
