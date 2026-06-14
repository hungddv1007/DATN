package datn_gym.controller;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    // POST /api/member/memberships — Đăng ký gói tập
    @PostMapping
    public ResponseEntity<MembershipResponse> registerPackage(
            Authentication authentication,
            @Valid @RequestBody MembershipRequest request) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.registerPackage(email, request));
    }

    // GET /api/member/memberships/current — Xem gói tập hiện tại
    @GetMapping("/current")
    public ResponseEntity<MembershipResponse> getCurrentMembership(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(membershipService.getMyCurrentMembership(email));
    }

    // GET /api/member/memberships/history — Lịch sử đăng ký
    @GetMapping("/history")
    public ResponseEntity<List<MembershipResponse>> getMembershipHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(membershipService.getMyMembershipHistory(email));
    }
}
