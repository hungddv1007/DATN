package datn_gym.controller;

import datn_gym.dto.request.LoginRequest;
import datn_gym.dto.request.RegisterRequest;
import datn_gym.dto.response.JwtResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/auth/test - API test kiểm tra server chạy
    @GetMapping("/test")
    public ResponseEntity<MessageResponse> test() {
        return ResponseEntity.ok(new MessageResponse("GymPro API is running!"));
    }
}