package datn_gym.controller;

import datn_gym.dto.request.NotificationCreateRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.NotificationResponse;
import datn_gym.dto.response.UnreadCountResponse;
import datn_gym.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ================================================================
    // Dùng chung cho mọi role đã đăng nhập (Admin/PT/Member)
    // ================================================================

    // GET /api/notifications?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        userDetails.getUsername(), pageable));
    }

    // GET /api/notifications/unread
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userDetails.getUsername()));
    }

    // GET /api/notifications/unread/count
    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getUnreadCount(userDetails.getUsername()));
    }

    // PUT /api/notifications/{notificationId}/read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<MessageResponse> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer notificationId) {
        notificationService.markAsRead(userDetails.getUsername(), notificationId);
        return ResponseEntity.ok(new MessageResponse("Đã đánh dấu đã đọc"));
    }

    // PUT /api/notifications/read-all
    @PutMapping("/read-all")
    public ResponseEntity<MessageResponse> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Đã đánh dấu tất cả đã đọc"));
    }

    // DELETE /api/notifications/{notificationId}
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<MessageResponse> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer notificationId) {
        notificationService.deleteNotification(userDetails.getUsername(), notificationId);
        return ResponseEntity.ok(new MessageResponse("Xóa thông báo thành công"));
    }

    // ================================================================
    // ADMIN / PT: Gửi thông báo cho user khác
    // ================================================================

    // POST /api/notifications/send
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'PT')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody NotificationCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendNotification(
                        userDetails.getUsername(), request));
    }
}
