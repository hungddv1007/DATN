package datn_gym.controller;

import datn_gym.dto.request.UpdatePtProfileRequest;
import datn_gym.dto.response.PtProfileResponse;
import datn_gym.service.PtProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PtProfileController {

    private final PtProfileService ptProfileService;

    // ----------------------------------------------------------------
    // PUBLIC APIs - Không cần đăng nhập
    // ----------------------------------------------------------------

    @GetMapping("/api/pt-profiles")
    public ResponseEntity<List<PtProfileResponse>> getAllPtProfiles() {
        return ResponseEntity.ok(ptProfileService.getAllPtProfiles());
    }

    @GetMapping("/api/pt-profiles/{userId}")
    public ResponseEntity<PtProfileResponse> getPtProfileByUserId(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(ptProfileService.getPtProfileByUserId(userId));
    }

    // ----------------------------------------------------------------
    // PT APIs - Chỉ PT mới được dùng
    // ----------------------------------------------------------------

    @GetMapping("/api/pt/profile")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ptProfileService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/api/pt/profile")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePtProfileRequest request) {
        return ResponseEntity.ok(
                ptProfileService.updateMyProfile(userDetails.getUsername(), request));
    }
}