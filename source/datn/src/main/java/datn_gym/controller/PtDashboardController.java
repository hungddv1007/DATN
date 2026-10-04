package datn_gym.controller;

import datn_gym.dto.response.PtDashboardResponse;
import datn_gym.dto.response.PtMemberResponse;
import datn_gym.service.PtDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pt")
@RequiredArgsConstructor
public class PtDashboardController {

    private final PtDashboardService ptDashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtDashboardResponse> getDashboardStats(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ptDashboardService.getDashboardStats(email));
    }

    @GetMapping("/members")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<PtMemberResponse>> getAssignedMembers(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ptDashboardService.getAssignedMembers(email));
    }
}
