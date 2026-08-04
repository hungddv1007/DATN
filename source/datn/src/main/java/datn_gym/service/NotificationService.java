package datn_gym.service;

import datn_gym.dto.request.NotificationBulkCreateRequest;
import datn_gym.dto.request.NotificationCreateRequest;
import datn_gym.dto.response.NotificationResponse;
import datn_gym.dto.response.UnreadCountResponse;
import datn_gym.entity.Notification;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.NotificationRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MembershipRepository membershipRepository;

    // ================================================================
    // USER: Xem thông báo của chính mình (mọi role: Admin/PT/Member)
    // ================================================================

    // Xem danh sách thông báo có phân trang
    public Page<NotificationResponse> getMyNotifications(String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        return notificationRepository
                .findByUser_IdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    // Xem danh sách thông báo chưa đọc
    public List<NotificationResponse> getUnreadNotifications(String email) {
        User user = userService.getUserByEmail(email);
        return notificationRepository
                .findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Đếm số thông báo chưa đọc — dùng cho badge
    public UnreadCountResponse getUnreadCount(String email) {
        User user = userService.getUserByEmail(email);
        long count = notificationRepository.countByUser_IdAndIsReadFalse(user.getId());
        return UnreadCountResponse.builder().unreadCount(count).build();
    }

    // Đánh dấu 1 thông báo đã đọc
    @Transactional
    public void markAsRead(String email, Integer notificationId) {
        User user = userService.getUserByEmail(email);

        // FIX: Phân biệt 404 vs không thuộc quyền
        // markAsRead() trả về số dòng update — 0 nghĩa là không tồn tại
        // hoặc không thuộc user này (IDOR check ngay trong câu UPDATE)
        int updated = notificationRepository.markAsRead(notificationId, user.getId());
        if (updated == 0) {
            // Phân biệt rõ: không tồn tại hay không phải của mình
            if (!notificationRepository.existsById(notificationId)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy thông báo");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn không có quyền với thông báo này");
        }
    }

    // Đánh dấu tất cả thông báo đã đọc
    @Transactional
    public void markAllAsRead(String email) {
        User user = userService.getUserByEmail(email);
        notificationRepository.markAllAsRead(user.getId());
    }

    // Xóa 1 thông báo của chính mình
    @Transactional
    public void deleteNotification(String email, Integer notificationId) {
        User user = userService.getUserByEmail(email);

        // FIX: Phân biệt 404 vs 403
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy thông báo");
        }

        // FIX IDOR: 1 câu SQL vừa tìm vừa check ownership
        Notification notification = notificationRepository
                .findByIdAndUser_Id(notificationId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn không có quyền xóa thông báo này"));

        notificationRepository.delete(notification);
    }

    // ================================================================
    // ADMIN / PT: Gửi thông báo cho user khác (có sender, qua API)
    // ================================================================

    @Transactional
    public NotificationResponse sendNotification(String senderEmail,
                                                  NotificationCreateRequest request) {
        User sender = getAuthorizedSender(senderEmail);
        User receiver = getUserById(request.getUserId());
        validateReceiverPermission(sender, receiver);

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(sender)
                .title(request.getTitle())
                .message(request.getMessage())
                .isRead(false)
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public int sendNotifications(String senderEmail,
                                 NotificationBulkCreateRequest request) {
        User sender = getAuthorizedSender(senderEmail);
        LinkedHashSet<Integer> uniqueReceiverIds =
                new LinkedHashSet<>(request.getUserIds());

        List<User> foundReceivers = userRepository.findAllById(uniqueReceiverIds);
        if (foundReceivers.size() != uniqueReceiverIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Có người nhận không tồn tại");
        }

        Map<Integer, User> receiverById = foundReceivers.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<User> receivers = uniqueReceiverIds.stream()
                .map(receiverById::get)
                .toList();

        receivers.forEach(receiver ->
                validateReceiverPermission(sender, receiver));

        List<Notification> notifications = receivers.stream()
                .map(receiver -> Notification.builder()
                        .user(receiver)
                        .sender(sender)
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    // ================================================================
    // SYSTEM: Gửi thông báo tự động từ background (CronJob, sự kiện hệ thống)
    // Không có user đăng nhập -> sender = null
    // Dùng cho: nhắc hết hạn gói tập, xác nhận thanh toán thành công, v.v.
    // ================================================================

    @Transactional
    public void sendSystemNotification(Integer receiverId, String title, String message) {
        User receiver = getUserById(receiverId);

        Notification notification = Notification.builder()
                .user(receiver)
                .sender(null) // Hệ thống tự động gửi, không có người gửi cụ thể
                .title(title)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người nhận"));
    }

    private User getAuthorizedSender(String senderEmail) {
        User sender = userService.getUserByEmail(senderEmail);
        if (sender.getRole() == null
                || !("ADMIN".equals(sender.getRole().getName())
                || "PT".equals(sender.getRole().getName()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản không có quyền gửi thông báo");
        }
        return sender;
    }

    private void validateReceiverPermission(User sender, User receiver) {
        if (!"PT".equals(sender.getRole().getName())) {
            return;
        }

        boolean managesReceiver = receiver.getRole() != null
                && "MEMBER".equals(receiver.getRole().getName())
                && membershipRepository.existsActiveMembershipByPtAndMember(
                        sender.getId(), receiver.getId());
        if (!managesReceiver) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn chỉ có thể gửi thông báo cho hội viên đang được phân công");
        }
    }

    // FIX N+1: @EntityGraph đã load user + sender sẵn trong Repository
    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .senderId(notification.getSender() != null
                        ? notification.getSender().getId() : null)
                .senderName(notification.getSender() != null
                        ? notification.getSender().getFullName() : "Hệ thống")
                .build();
    }
}
