package datn_gym.controller;

import datn_gym.dto.request.MemberProfileUpdateRequest;
import datn_gym.dto.response.MemberProfileResponse;
import datn_gym.service.MemberProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    // ================================================================
    // MEMBER APIs
    // ================================================================

    // GET /api/member/profile/physical
    // Member xem hồ sơ thể chất của chính mình
    @GetMapping("/api/member/profile/physical")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                memberProfileService.getMyProfile(userDetails.getUsername()));
    }

    // ================================================================
    // PT APIs
    // ================================================================

    // GET /api/pt/member-profiles/{memberId}
    // PT xem hồ sơ thể chất của một hội viên thuộc quyền mình
    @GetMapping("/api/pt/member-profiles/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MemberProfileResponse> getMemberProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(
                memberProfileService.getMemberProfile(
                        userDetails.getUsername(), memberId));
    }

    // PUT /api/pt/member-profiles/{memberId}
    // PT ghi nhận / cập nhật tình trạng thể chất của hội viên
    @PutMapping("/api/pt/member-profiles/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MemberProfileResponse> updateMemberProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId,
            @Valid @RequestBody MemberProfileUpdateRequest request) {
        return ResponseEntity.ok(
                memberProfileService.updateMemberProfile(
                        userDetails.getUsername(), memberId, request));
    }
}
