package datn_gym.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.security.CustomUserDetailsService;
import datn_gym.security.JwtTokenProvider;
import datn_gym.service.ChatWebSocketBroker;
import datn_gym.service.HumanChatService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final String AUTH_EMAIL = "chat.auth.email";
    private static final String AUTH_ROLE = "chat.auth.role";
    private static final String AUTH_TOKEN = "chat.auth.token";

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final HumanChatService humanChatService;
    private final ChatWebSocketBroker broker;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        send(session, Map.of("type", "AUTH_REQUIRED"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode payload = objectMapper.readTree(message.getPayload());
            String type = payload.path("type").asText("").trim().toUpperCase();
            switch (type) {
                case "AUTH" -> authenticate(session, payload.path("token").asText(""));
                case "SUBSCRIBE" -> subscribe(session, integer(payload, "conversationId"));
                case "UNSUBSCRIBE" -> unsubscribe(session, integer(payload, "conversationId"));
                case "SEND_MESSAGE" -> sendChatMessage(
                        session,
                        integer(payload, "conversationId"),
                        payload.path("message").asText(""));
                case "PING" -> {
                    requireAuthenticated(session);
                    send(session, Map.of("type", "PONG"));
                }
                default -> sendError(session, "Loại sự kiện WebSocket không hợp lệ.");
            }
        } catch (ExpiredJwtException ex) {
            sendError(session, "Phiên đăng nhập đã hết hạn.");
            close(session, CloseStatus.POLICY_VIOLATION);
        } catch (SocketAuthenticationException ex) {
            sendError(session, ex.getMessage());
            close(session, CloseStatus.POLICY_VIOLATION);
        } catch (Exception ex) {
            sendError(session, ex.getMessage() == null ? "Không thể xử lý yêu cầu WebSocket." : ex.getMessage());
        }
    }

    private void authenticate(WebSocketSession session, String token) {
        if (token.isBlank() || !tokenProvider.validateToken(token)) {
            throw new SocketAuthenticationException("Token đăng nhập không hợp lệ.");
        }
        String email = tokenProvider.getEmailFromToken(token);
        UserDetails details = userDetailsService.loadUserByUsername(email);
        String role = details.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElse("");
        if (!"MEMBER".equals(role) && !"SALE".equals(role)) {
            throw new SocketAuthenticationException(
                    "WebSocket chat chỉ dành cho hội viên và nhân viên Sale.");
        }

        broker.removeSession(session);
        session.getAttributes().put(AUTH_EMAIL, email);
        session.getAttributes().put(AUTH_ROLE, role);
        session.getAttributes().put(AUTH_TOKEN, token);
        broker.registerAuthenticatedSession(email, session);
        send(session, Map.of("type", "AUTHENTICATED", "role", role));
    }

    private void subscribe(WebSocketSession session, Integer conversationId) {
        AuthenticatedSocket user = requireAuthenticated(session);
        humanChatService.assertWebSocketAccess(user.email(), user.role(), conversationId);
        broker.subscribe(conversationId, session);
        send(session, Map.of("type", "SUBSCRIBED", "conversationId", conversationId));
    }

    private void unsubscribe(WebSocketSession session, Integer conversationId) {
        requireAuthenticated(session);
        broker.unsubscribe(conversationId, session);
        send(session, Map.of("type", "UNSUBSCRIBED", "conversationId", conversationId));
    }

    private void sendChatMessage(WebSocketSession session, Integer conversationId, String text) {
        AuthenticatedSocket user = requireAuthenticated(session);
        String cleanText = text == null ? "" : text.trim();
        if (cleanText.isEmpty()) throw new IllegalArgumentException("Tin nhắn không được để trống.");
        if (cleanText.length() > 2_000) throw new IllegalArgumentException("Tin nhắn không vượt quá 2000 ký tự.");

        humanChatService.assertWebSocketAccess(user.email(), user.role(), conversationId);
        if ("MEMBER".equals(user.role())) {
            humanChatService.sendMemberMessage(user.email(), conversationId, cleanText);
        } else {
            humanChatService.sendSaleMessage(user.email(), conversationId, cleanText);
        }
    }

    private AuthenticatedSocket requireAuthenticated(WebSocketSession session) {
        String email = (String) session.getAttributes().get(AUTH_EMAIL);
        String role = (String) session.getAttributes().get(AUTH_ROLE);
        String token = (String) session.getAttributes().get(AUTH_TOKEN);
        if (email == null || role == null || token == null || !tokenProvider.validateToken(token)) {
            throw new SocketAuthenticationException("Kết nối WebSocket chưa được xác thực.");
        }
        return new AuthenticatedSocket(email, role);
    }

    private Integer integer(JsonNode payload, String field) {
        if (!payload.hasNonNull(field) || !payload.path(field).canConvertToInt()) {
            throw new IllegalArgumentException("Thiếu " + field + ".");
        }
        return payload.path(field).asInt();
    }

    private void sendError(WebSocketSession session, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "ERROR");
        payload.put("message", message);
        send(session, payload);
    }

    private void send(WebSocketSession session, Map<String, Object> payload) {
        if (!session.isOpen()) return;
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (IOException | IllegalStateException ignored) {
            broker.removeSession(session);
        }
    }

    private void close(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException ignored) {
            // Kết nối đã đóng.
        } finally {
            broker.removeSession(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broker.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        broker.removeSession(session);
        close(session, CloseStatus.SERVER_ERROR);
    }

    private record AuthenticatedSocket(String email, String role) {
    }

    private static class SocketAuthenticationException extends RuntimeException {
        private SocketAuthenticationException(String message) {
            super(message);
        }
    }
}
