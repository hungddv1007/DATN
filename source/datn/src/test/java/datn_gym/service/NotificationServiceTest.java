package datn_gym.service;

import datn_gym.dto.request.NotificationCreateRequest;
import datn_gym.dto.request.NotificationBulkCreateRequest;
import datn_gym.entity.Notification;
import datn_gym.entity.Role;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.NotificationRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private MembershipRepository membershipRepository;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(
                notificationRepository,
                userRepository,
                userService,
                membershipRepository);
    }

    @Test
    void ptCannotNotifyMemberWhoIsNotAssignedToThem() {
        User pt = user(10, "pt@gym.local", "PT");
        User member = user(20, "member@gym.local", "MEMBER");
        NotificationCreateRequest request = request(member.getId());
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), member.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.sendNotification(pt.getEmail(), request))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void ptCanNotifyTheirAssignedMember() {
        User pt = user(10, "pt@gym.local", "PT");
        User member = user(20, "member@gym.local", "MEMBER");
        NotificationCreateRequest request = request(member.getId());
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), member.getId())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.sendNotification(pt.getEmail(), request);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void adminCanNotifyMultipleDistinctUsersInOneRequest() {
        User admin = user(1, "admin@gym.local", "ADMIN");
        User pt = user(10, "pt@gym.local", "PT");
        User member = user(20, "member@gym.local", "MEMBER");
        NotificationBulkCreateRequest request = bulkRequest(
                List.of(pt.getId(), member.getId(), pt.getId()));
        when(userService.getUserByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepository.findAllById(any()))
                .thenReturn(List.of(pt, member));

        int sentCount = service.sendNotifications(admin.getEmail(), request);

        assertThat(sentCount).isEqualTo(2);
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void ptBulkNotificationIsRejectedWhenAnyMemberIsNotAssigned() {
        User pt = user(10, "pt@gym.local", "PT");
        User assignedMember = user(20, "member1@gym.local", "MEMBER");
        User unassignedMember = user(21, "member2@gym.local", "MEMBER");
        NotificationBulkCreateRequest request = bulkRequest(
                List.of(assignedMember.getId(), unassignedMember.getId()));
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(userRepository.findAllById(any()))
                .thenReturn(List.of(assignedMember, unassignedMember));
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), assignedMember.getId())).thenReturn(true);
        when(membershipRepository.existsActiveMembershipByPtAndMember(
                pt.getId(), unassignedMember.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.sendNotifications(pt.getEmail(), request))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));

        verify(notificationRepository, never()).saveAll(anyList());
    }

    private User user(int id, String email, String roleName) {
        return User.builder()
                .id(id)
                .email(email)
                .fullName(email)
                .role(Role.builder().name(roleName).build())
                .build();
    }

    private NotificationCreateRequest request(int userId) {
        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setUserId(userId);
        request.setTitle("Lịch tập mới");
        request.setMessage("Bạn có lịch tập mới.");
        return request;
    }

    private NotificationBulkCreateRequest bulkRequest(List<Integer> userIds) {
        NotificationBulkCreateRequest request = new NotificationBulkCreateRequest();
        request.setUserIds(userIds);
        request.setTitle("Thông báo chung");
        request.setMessage("Nội dung thông báo chung.");
        return request;
    }
}
