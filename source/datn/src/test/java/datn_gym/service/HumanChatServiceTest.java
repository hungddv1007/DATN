package datn_gym.service;

import datn_gym.entity.AiConversation;
import datn_gym.entity.AiMessage;
import datn_gym.entity.Role;
import datn_gym.entity.User;
import datn_gym.repository.AiConversationRepository;
import datn_gym.repository.AiMessageRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumanChatServiceTest {

    @Mock AiConversationRepository conversationRepository;
    @Mock AiMessageRepository messageRepository;
    @Mock UserRepository userRepository;
    @Mock SaleService saleService;
    @Mock NotificationService notificationService;
    @Mock ChatWebSocketBroker webSocketBroker;
    @InjectMocks HumanChatService humanChatService;

    @Test
    void saleMessageIsStoredAndBroadcastImmediately() {
        Role saleRole = Role.builder().name("SALE").build();
        User sale = User.builder().id(8).email("sale@gympro.com").fullName("Sale")
                .role(saleRole).build();
        User member = User.builder().id(9).email("member@gympro.com").fullName("Member").build();
        AiConversation conversation = AiConversation.builder().id(15).user(member)
                .assignedSale(sale).handoffStatus("SALE_ASSIGNED").build();
        when(userRepository.findByEmail(sale.getEmail())).thenReturn(Optional.of(sale));
        when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(AiMessage.class))).thenAnswer(invocation -> {
            AiMessage message = invocation.getArgument(0);
            message.setId(100L);
            return message;
        });
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        var result = humanChatService.sendSaleMessage(
                sale.getEmail(), conversation.getId(), "Xin chào hội viên");

        assertEquals(100L, result.getId());
        assertEquals("SALE", result.getRole());
        assertEquals("SALE_JOINED", conversation.getHandoffStatus());
        verify(webSocketBroker).publishMessage(any(AiMessage.class));
        verify(webSocketBroker).publishConversation(conversation);
    }
}
