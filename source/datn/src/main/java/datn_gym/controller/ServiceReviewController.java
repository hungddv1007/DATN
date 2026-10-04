package datn_gym.controller;

import datn_gym.dto.request.*;
import datn_gym.dto.response.ServiceReviewResponse;
import datn_gym.service.ServiceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceReviewController {
    private final ServiceReviewService service;

    @GetMapping("/api/public/service-reviews/featured")
    public ResponseEntity<List<ServiceReviewResponse>> featured() { return ResponseEntity.ok(service.getFeatured()); }

    @GetMapping("/api/member/service-reviews")
    public ResponseEntity<List<ServiceReviewResponse>> mine(Authentication auth) { return ResponseEntity.ok(service.getMine(auth.getName())); }

    @PostMapping("/api/member/service-reviews")
    public ResponseEntity<ServiceReviewResponse> create(Authentication auth, @Valid @RequestBody ServiceReviewRequest request) {
        return ResponseEntity.ok(service.create(auth.getName(), request));
    }

    @GetMapping("/api/admin/service-reviews")
    public ResponseEntity<List<ServiceReviewResponse>> all() { return ResponseEntity.ok(service.getAllForAdmin()); }

    @PutMapping("/api/admin/service-reviews/{id}/featured")
    public ResponseEntity<ServiceReviewResponse> setFeatured(@PathVariable Integer id,
            @RequestBody ServiceReviewFeaturedRequest request) {
        return ResponseEntity.ok(service.setFeatured(id, request));
    }
}
