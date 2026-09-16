package datn_gym.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.security.CustomUserDetailsService;
import datn_gym.security.JwtTokenProvider;
import datn_gym.service.ChatWebSocketBroker;
import datn_gym.service.HumanChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatWebSocketHandlerTest {

    private final JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    private final CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
    private final HumanChatService humanChatService = mock(HumanChatService.class);
    private final ChatWebSocketBroker broker = mock(ChatWebSocketBroker.class);
    private final WebSocketSession session = mock(WebSocketSession.class);
    private final Map<String, Object> attributes = new HashMap<>();
    private ChatWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ChatWebSocketHandler(
                new ObjectMapper(), tokenProvider, userDetailsService, humanChatService, broker);
        when(session.getAttributes()).thenReturn(attributes);
        when(session.isOpen()).thenReturn(true);
    }

    @Test
    void authenticatedMemberCanSubscribeAndSendMessage() {
        when(tokenProvider.validateToken("valid-token")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("valid-token")).thenReturn("member@gympro.com");
        when(userDetailsService.loadUserByUsername("member@gympro.com"))
                .thenReturn(User.withUsername("member@gympro.com").password("password")
                        .roles("MEMBER").build());

        handler.handleTextMessage(session,
                new TextMessage("{\"type\":\"AUTH\",\"token\":\"valid-token\"}"));
        handler.handleTextMessage(session,
                new TextMessage("{\"type\":\"SUBSCRIBE\",\"conversationId\":12}"));
        handler.handleTextMessage(session,
                new TextMessage("{\"type\":\"SEND_MESSAGE\",\"conversationId\":12,\"message\":\"Xin chào\"}"));

        verify(broker).registerAuthenticatedSession("member@gympro.com", session);
        verify(humanChatService, times(2))
                .assertWebSocketAccess("member@gympro.com", "MEMBER", 12);
        verify(broker).subscribe(12, session);
        verify(humanChatService).sendMemberMessage("member@gympro.com", 12, "Xin chào");
    }

    @Test
    void unauthenticatedHeartbeatIsRejectedAndConnectionIsClosed() throws Exception {
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"PING\"}"));

        verify(session).close(CloseStatus.POLICY_VIOLATION);
        verify(broker).removeSession(session);
    }
}
