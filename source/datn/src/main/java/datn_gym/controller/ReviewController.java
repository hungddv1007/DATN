package datn_gym.controller;

import datn_gym.dto.request.ReviewRequest;
import datn_gym.dto.request.ReviewUpdateRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.ReviewResponse;
import datn_gym.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ----------------------------------------------------------------
    // PUBLIC: Xem đánh giá của một PT (trang hồ sơ PT công khai)
    // ----------------------------------------------------------------

    // GET /api/pt-profiles/{ptId}/reviews
    @GetMapping("/api/pt-profiles/{ptId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPt(
            @PathVariable Integer ptId) {
        return ResponseEntity.ok(reviewService.getReviewsByPt(ptId));
    }

    // ----------------------------------------------------------------
    // MEMBER APIs
    // ----------------------------------------------------------------

    // GET /api/hoi-vien/reviews
    // Member xem tất cả đánh giá mình đã gửi
    @GetMapping("/api/hoi-vien/reviews")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                reviewService.getMyReviews(userDetails.getUsername()));
    }

    // POST /api/hoi-vien/reviews
    // Member tạo đánh giá mới
    @PostMapping("/api/hoi-vien/reviews")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(userDetails.getUsername(), request));
    }

    // PUT /api/hoi-vien/reviews/{reviewId}
    // Member sửa đánh giá — chỉ gửi ratingStar và comment
    @PutMapping("/api/hoi-vien/reviews/{reviewId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(
                reviewService.updateReview(
                        userDetails.getUsername(), reviewId, request));
    }

    // DELETE /api/hoi-vien/reviews/{reviewId}
    // Member xóa đánh giá
    @DeleteMapping("/api/hoi-vien/reviews/{reviewId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MessageResponse> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer reviewId) {
        reviewService.deleteReview(userDetails.getUsername(), reviewId);
        return ResponseEntity.ok(new MessageResponse("Xóa đánh giá thành công"));
    }
}
