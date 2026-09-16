package datn_gym.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.entity.AiConversation;
import datn_gym.entity.AiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatWebSocketBroker {

    private final ObjectMapper objectMapper;
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final Map<Integer, Set<WebSocketSession>> conversationSessions = new ConcurrentHashMap<>();

    public void registerAuthenticatedSession(String email, WebSocketSession session) {
        userSessions.computeIfAbsent(email, ignored -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void subscribe(Integer conversationId, WebSocketSession session) {
        conversationSessions.computeIfAbsent(conversationId, ignored -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void unsubscribe(Integer conversationId, WebSocketSession session) {
        Set<WebSocketSession> sessions = conversationSessions.get(conversationId);
        if (sessions == null) return;
        sessions.remove(session);
        if (sessions.isEmpty()) conversationSessions.remove(conversationId, sessions);
    }

    public void removeSession(WebSocketSession session) {
        userSessions.forEach((email, sessions) -> {
            sessions.remove(session);
            if (sessions.isEmpty()) userSessions.remove(email, sessions);
        });
        conversationSessions.forEach((conversationId, sessions) -> {
            sessions.remove(session);
            if (sessions.isEmpty()) conversationSessions.remove(conversationId, sessions);
        });
    }

    public void publishMessage(AiMessage message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "MESSAGE");
        payload.put("conversationId", message.getConversation().getId());
        payload.put("message", messagePayload(message));
        afterCommit(() -> broadcast(
                conversationSessions.get(message.getConversation().getId()), payload));
    }

    public void publishConversation(AiConversation conversation) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "CONVERSATION_UPDATED");
        payload.put("conversation", conversationPayload(conversation));

        Integer conversationId = conversation.getId();
        String memberEmail = conversation.getUser().getEmail();
        String saleEmail = conversation.getAssignedSale() == null
                ? null : conversation.getAssignedSale().getEmail();
        afterCommit(() -> {
            Set<WebSocketSession> recipients = new HashSet<>();
            addAll(recipients, conversationSessions.get(conversationId));
            addAll(recipients, userSessions.get(memberEmail));
            if (saleEmail != null) addAll(recipients, userSessions.get(saleEmail));
            broadcast(recipients, payload);
        });
    }

    private Map<String, Object> messagePayload(AiMessage message) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", message.getId());
        value.put("role", message.getRole());
        value.put("content", message.getContent());
        value.put("model", message.getModel());
        value.put("senderUserId", message.getSenderUser() == null ? null : message.getSenderUser().getId());
        value.put("senderName", message.getSenderUser() == null ? null : message.getSenderUser().getFullName());
        value.put("createdAt", message.getCreatedAt() == null ? null : message.getCreatedAt().toString());
        return value;
    }

    private Map<String, Object> conversationPayload(AiConversation conversation) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", conversation.getId());
        value.put("title", conversation.getTitle());
        value.put("physicalDataConsent", Boolean.TRUE.equals(conversation.getPhysicalDataConsent()));
        value.put("saleDataConsent", Boolean.TRUE.equals(conversation.getSaleDataConsent()));
        value.put("handoffStatus", conversation.getHandoffStatus());
        value.put("assignedSaleId", conversation.getAssignedSale() == null ? null : conversation.getAssignedSale().getId());
        value.put("assignedSaleName", conversation.getAssignedSale() == null ? null : conversation.getAssignedSale().getFullName());
        value.put("createdAt", conversation.getCreatedAt() == null ? null : conversation.getCreatedAt().toString());
        value.put("updatedAt", conversation.getUpdatedAt() == null ? null : conversation.getUpdatedAt().toString());
        value.put("handoffAt", conversation.getHandoffAt() == null ? null : conversation.getHandoffAt().toString());
        return value;
    }

    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    private void broadcast(Set<WebSocketSession> sessions, Map<String, Object> payload) {
        if (sessions == null || sessions.isEmpty()) return;
        final String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception ignored) {
            return;
        }
        sessions.forEach(session -> send(session, json));
    }

    private void send(WebSocketSession session, String json) {
        if (session == null || !session.isOpen()) {
            if (session != null) removeSession(session);
            return;
        }
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }
        } catch (IOException | IllegalStateException ex) {
            removeSession(session);
        }
    }

    private void addAll(Set<WebSocketSession> target, Set<WebSocketSession> source) {
        if (source != null) target.addAll(source);
    }
}
