package datn_gym.service;

import datn_gym.dto.response.AiConversationResponse;
import datn_gym.dto.response.AiMessageResponse;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HumanChatService {
    private final AiConversationRepository conversationRepository;
    private final AiMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SaleService saleService;
    private final NotificationService notificationService;

    @Transactional
    public AiConversationResponse requestHandoff(String memberEmail, Integer conversationId) {
        User member = requireUser(memberEmail, "MEMBER");
        AiConversation conversation = conversationRepository.findByIdAndUser_Id(conversationId, member.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc trò chuyện"));
        if (List.of("WAITING_SALE", "SALE_ASSIGNED", "SALE_JOINED").contains(conversation.getHandoffStatus())) {
            return toConversation(conversation);
        }
        conversation.setSaleDataConsent(true);
        conversation.setHandoffAt(LocalDateTime.now());
        conversation.setClosedAt(null);
        SaleProfile profile = saleService.findBestAvailableSale();
        if (profile == null) {
            conversation.setAssignedSale(null);
            conversation.setHandoffStatus("WAITING_SALE");
        } else {
            conversation.setAssignedSale(profile.getUser());
            conversation.setHandoffStatus("SALE_ASSIGNED");
            notificationService.sendSystemNotification(profile.getUser().getId(), "Yêu cầu tư vấn mới",
                    member.getFullName() + " đang chờ bạn tham gia cuộc trò chuyện.");
        }
        conversation.setUpdatedAt(LocalDateTime.now());
        addSystem(conversation, profile == null
                ? "Yêu cầu tư vấn đã được đưa vào hàng chờ."
                : "Nhân viên tư vấn " + profile.getUser().getFullName() + " đã được phân công.");
        return toConversation(conversationRepository.save(conversation));
    }

    public List<AiConversationResponse> saleConversations(String saleEmail) {
        SaleProfile profile = saleService.requireProfile(saleEmail);
        return conversationRepository.findByAssignedSale_IdAndHandoffStatusInOrderByUpdatedAtDesc(
                        profile.getUser().getId(), List.of("SALE_ASSIGNED", "SALE_JOINED"))
                .stream().map(this::toConversation).toList();
    }

    @Transactional
    public AiConversationResponse claimOldestWaiting(String saleEmail) {
        SaleProfile profile = saleService.requireProfile(saleEmail);
        long active = conversationRepository.countByAssignedSale_IdAndHandoffStatusIn(
                profile.getUser().getId(), List.of("SALE_ASSIGNED", "SALE_JOINED"));
        if (!Boolean.TRUE.equals(profile.getIsOnline())) throw new IllegalArgumentException("Hãy bật trạng thái trực tuyến trước");
        if (active >= profile.getMaxConcurrentChats()) throw new IllegalArgumentException("Bạn đã đạt tối đa 3 cuộc tư vấn đồng thời");
        AiConversation conversation = conversationRepository.findByHandoffStatusOrderByHandoffAtAsc("WAITING_SALE")
                .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Không có khách hàng đang chờ"));
        conversation.setAssignedSale(profile.getUser());
        conversation.setHandoffStatus("SALE_ASSIGNED");
        conversation.setUpdatedAt(LocalDateTime.now());
        addSystem(conversation, "Nhân viên tư vấn " + profile.getUser().getFullName() + " đã được phân công.");
        return toConversation(conversationRepository.save(conversation));
    }

    @Transactional
    public AiMessageResponse sendMemberMessage(String email, Integer conversationId, String text) {
        User member = requireUser(email, "MEMBER");
        AiConversation conversation = conversationRepository.findByIdAndUser_Id(conversationId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc trò chuyện"));
        requireHumanChat(conversation);
        return saveMessage(conversation, member, "USER", text);
    }

    @Transactional
    public AiMessageResponse sendSaleMessage(String email, Integer conversationId, String text) {
        User sale = requireUser(email, "SALE");
        AiConversation conversation = requireAssignedSale(conversationId, sale);
        if ("SALE_ASSIGNED".equals(conversation.getHandoffStatus())) {
            conversation.setHandoffStatus("SALE_JOINED");
        }
        return saveMessage(conversation, sale, "SALE", text);
    }

    public List<AiMessageResponse> saleMessages(String email, Integer conversationId) {
        User sale = requireUser(email, "SALE");
        AiConversation conversation = requireAssignedSale(conversationId, sale);
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId())
                .stream().map(this::toMessage).toList();
    }

    @Transactional
    public AiConversationResponse close(String email, Integer conversationId) {
        User sale = requireUser(email, "SALE");
        AiConversation conversation = requireAssignedSale(conversationId, sale);
        requireHumanChat(conversation);
        conversation.setHandoffStatus("CLOSED");
        conversation.setClosedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        addSystem(conversation, "Phiên tư vấn trực tiếp đã kết thúc. Bạn có thể tiếp tục trò chuyện với GymPro AI.");
        return toConversation(conversationRepository.save(conversation));
    }

    private AiMessageResponse saveMessage(AiConversation conversation, User sender, String role, String text) {
        AiMessage message = messageRepository.save(AiMessage.builder().conversation(conversation)
                .senderUser(sender).role(role).content(text.trim()).build());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        return toMessage(message);
    }

    private void addSystem(AiConversation conversation, String text) {
        messageRepository.save(AiMessage.builder().conversation(conversation).role("SYSTEM").content(text).build());
    }

    private void requireHumanChat(AiConversation conversation) {
        if (!List.of("SALE_ASSIGNED", "SALE_JOINED").contains(conversation.getHandoffStatus())) {
            throw new IllegalArgumentException("Cuộc trò chuyện chưa có nhân viên tư vấn đang phụ trách");
        }
    }

    private AiConversation requireAssignedSale(Integer id, User sale) {
        AiConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc trò chuyện"));
        if (conversation.getAssignedSale() == null || !conversation.getAssignedSale().getId().equals(sale.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cuộc trò chuyện không được phân công cho bạn");
        }
        return conversation;
    }

    private User requireUser(String email, String role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        if (!role.equals(user.getRole().getName())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return user;
    }

    private AiConversationResponse toConversation(AiConversation c) {
        return AiConversationResponse.builder().id(c.getId()).title(c.getTitle())
                .physicalDataConsent(Boolean.TRUE.equals(c.getPhysicalDataConsent()))
                .saleDataConsent(Boolean.TRUE.equals(c.getSaleDataConsent())).handoffStatus(c.getHandoffStatus())
                .assignedSaleId(c.getAssignedSale() == null ? null : c.getAssignedSale().getId())
                .assignedSaleName(c.getAssignedSale() == null ? null : c.getAssignedSale().getFullName())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt()).handoffAt(c.getHandoffAt()).build();
    }

    private AiMessageResponse toMessage(AiMessage m) {
        return AiMessageResponse.builder().id(m.getId()).role(m.getRole()).content(m.getContent()).model(m.getModel())
                .senderUserId(m.getSenderUser() == null ? null : m.getSenderUser().getId())
                .senderName(m.getSenderUser() == null ? null : m.getSenderUser().getFullName())
                .createdAt(m.getCreatedAt()).build();
    }
}
