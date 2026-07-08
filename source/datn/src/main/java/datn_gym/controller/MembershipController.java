package datn_gym.controller;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    // 1. Đăng ký gói mới
    @PostMapping("/register")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> registerPackage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MembershipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.registerPackage(userDetails.getUsername(), request));
    }

    // 2. Gia hạn cùng gói
    @PostMapping("/renew")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> renewPackage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.renewPackage(userDetails.getUsername(), request));
    }

    // 3. Nâng cấp tại chỗ (không thêm ngày)
    @PostMapping("/upgrade")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> upgradePackage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.upgradePackage(userDetails.getUsername(), request));
    }

    // 4. Nâng cấp + Gia hạn
    @PostMapping("/upgrade-renew")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> upgradeAndRenewPackage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.upgradeAndRenewPackage(userDetails.getUsername(), request));
    }

    // 5. Bảo lưu
    @PostMapping("/pause")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> pauseMembership(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(membershipService.pauseMembership(userDetails.getUsername()));
    }

    // 6. Hủy bảo lưu
    @PostMapping("/resume")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> resumeMembership(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(membershipService.resumeMembership(userDetails.getUsername()));
    }

    // 7. Hủy gói
    @PostMapping("/cancel")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MessageResponse> cancelMembership(
            @AuthenticationPrincipal UserDetails userDetails) {
        membershipService.cancelMembership(userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Đã hủy gói tập hiện tại."));
    }

    // ----------------------------------------------------------------
    // PREVIEW APIs
    // ----------------------------------------------------------------

    @GetMapping("/preview-upgrade")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> previewUpgrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer packageId) {
        return ResponseEntity.ok(membershipService.previewUpgrade(userDetails.getUsername(), packageId));
    }

    @GetMapping("/preview-renew")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> previewRenew(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer packageId,
            @RequestParam Integer days) {
        return ResponseEntity.ok(membershipService.previewRenew(userDetails.getUsername(), packageId, days));
    }

    // ----------------------------------------------------------------
    // GET APIs
    // ----------------------------------------------------------------

    @GetMapping("/current")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MembershipResponse> getMyCurrentMembership(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(membershipService.getMyCurrentMembership(userDetails.getUsername()));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<MembershipResponse>> getMyMembershipHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(membershipService.getMyMembershipHistory(userDetails.getUsername()));
    }
}
