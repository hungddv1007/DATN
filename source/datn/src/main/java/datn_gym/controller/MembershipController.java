package datn_gym.controller;

import datn_gym.dto.request.MembershipRequest;
import datn_gym.dto.request.PauseMembershipRequest;
import datn_gym.dto.request.RenewRequest;
import datn_gym.dto.request.UpgradeRequest;
import datn_gym.dto.response.MembershipResponse;
import datn_gym.dto.response.PricePreviewResponse;
import datn_gym.service.MembershipService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
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

    // POST — Đăng ký gói mới
    @PostMapping
    public ResponseEntity<MembershipResponse> registerPackage(
            Authentication auth,
            @Valid @RequestBody MembershipRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.registerPackage(auth.getName(), request,
                        clientIp(httpRequest), httpRequest.getHeader("User-Agent")));
    }

    // POST /renew — Gia hạn cùng gói
    @PostMapping("/renew")
    public ResponseEntity<MembershipResponse> renewMembership(
            Authentication auth,
            @Valid @RequestBody RenewRequest request) {
        return ResponseEntity.ok(membershipService.renewMembership(auth.getName(), request));
    }

    // POST /upgrade — Nâng cấp gói (có/không gia hạn thêm)
    @PostMapping("/upgrade")
    public ResponseEntity<MembershipResponse> upgradeMembership(
            Authentication auth,
            @Valid @RequestBody UpgradeRequest request) {
        return ResponseEntity.ok(membershipService.upgradeMembership(auth.getName(), request));
    }

    // POST /pause — Bảo lưu
    @PostMapping("/pause")
    public ResponseEntity<MembershipResponse> pauseMembership(
            Authentication auth,
            @Valid @RequestBody PauseMembershipRequest request) {
        return ResponseEntity.ok(membershipService.pauseMembership(auth.getName(), request));
    }

    // POST /resume — Huỷ bảo lưu
    @PostMapping("/resume")
    public ResponseEntity<MembershipResponse> resumeMembership(Authentication auth) {
        return ResponseEntity.ok(membershipService.resumeMembership(auth.getName()));
    }

    // POST /cancel — Huỷ gói
    // GET /current — Gói tập hiện tại
    @GetMapping("/current")
    public ResponseEntity<MembershipResponse> getCurrentMembership(Authentication auth) {
        return ResponseEntity.ok(membershipService.getMyCurrentMembership(auth.getName()));
    }

    // GET /history — Lịch sử đăng ký
    @GetMapping("/history")
    public ResponseEntity<List<MembershipResponse>> getMembershipHistory(Authentication auth) {
        return ResponseEntity.ok(membershipService.getMyMembershipHistory(auth.getName()));
    }

    // GET /preview/purchase — Xem trước chính xác giá mua và mã áp dụng
    @GetMapping("/preview/purchase")
    public ResponseEntity<PricePreviewResponse> previewPurchase(
            Authentication auth,
            @RequestParam int packageId,
            @RequestParam int days,
            @RequestParam(required = false) String promotionCode,
            @RequestParam(required = false) String referralCode) {
        return ResponseEntity.ok(membershipService.previewPurchase(
                auth.getName(), packageId, days, promotionCode, referralCode));
    }

    // GET /preview/renew?days=90 — Xem trước giá gia hạn
    @GetMapping("/preview/renew")
    public ResponseEntity<PricePreviewResponse> previewRenew(
            Authentication auth,
            @RequestParam int days) {
        return ResponseEntity.ok(membershipService.previewRenew(auth.getName(), days));
    }

    // GET /preview/upgrade?packageId=2&extraDays=30 — Xem trước giá nâng cấp
    @GetMapping("/preview/upgrade")
    public ResponseEntity<PricePreviewResponse> previewUpgrade(
            Authentication auth,
            @RequestParam int packageId,
            @RequestParam(required = false) Integer extraDays) {
        return ResponseEntity.ok(membershipService.previewUpgrade(auth.getName(), packageId, extraDays));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded == null || forwarded.isBlank()
                ? request.getRemoteAddr()
                : forwarded.split(",")[0].trim();
    }
}
