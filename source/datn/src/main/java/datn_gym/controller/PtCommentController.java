package datn_gym.controller;

import datn_gym.dto.request.PtCommentCreateRequest;
import datn_gym.dto.request.PtCommentUpdateRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.PtCommentResponse;
import datn_gym.service.PtCommentService;
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
public class PtCommentController {

    private final PtCommentService ptCommentService;

    // ----------------------------------------------------------------
    // PT APIs
    // ----------------------------------------------------------------

    @GetMapping("/api/pt/comments")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<PtCommentResponse>> getAllMyComments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ptCommentService.getAllMyComments(userDetails.getUsername()));
    }

    @GetMapping("/api/pt/comments/member/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<PtCommentResponse>> getCommentsByMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(
                ptCommentService.getCommentsByMember(userDetails.getUsername(), memberId));
    }

    @PostMapping("/api/pt/comments")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtCommentResponse> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PtCommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ptCommentService.createComment(userDetails.getUsername(), request));
    }

    // Clean Code: dùng PtCommentUpdateRequest — chỉ cần content, không cần memberId
    @PutMapping("/api/pt/comments/{commentId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<PtCommentResponse> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer commentId,
            @Valid @RequestBody PtCommentUpdateRequest request) {
        return ResponseEntity.ok(
                ptCommentService.updateComment(
                        userDetails.getUsername(), commentId, request));
    }

    @DeleteMapping("/api/pt/comments/{commentId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer commentId) {
        ptCommentService.deleteComment(userDetails.getUsername(), commentId);
        return ResponseEntity.ok(new MessageResponse("Xóa nhận xét thành công"));
    }

    // ----------------------------------------------------------------
    // MEMBER APIs
    // ----------------------------------------------------------------

    @GetMapping("/api/member/comments")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<PtCommentResponse>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ptCommentService.getMyComments(userDetails.getUsername()));
    }

    @GetMapping("/api/member/comments/route/{routeId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<PtCommentResponse>> getMyCommentsByRoute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer routeId) {
        return ResponseEntity.ok(
                ptCommentService.getMyCommentsByRoute(
                        userDetails.getUsername(), routeId));
    }
}