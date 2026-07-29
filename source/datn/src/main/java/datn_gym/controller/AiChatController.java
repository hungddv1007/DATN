package datn_gym.controller;

import datn_gym.dto.request.AiChatRequest;
import datn_gym.dto.request.AiConsentRequest;
import datn_gym.dto.request.AiConversationCreateRequest;
import datn_gym.dto.response.AiConversationResponse;
import datn_gym.dto.response.AiMessageResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.AiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/member/ai")
@PreAuthorize("hasRole('MEMBER')")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/conversations")
    public ResponseEntity<AiConversationResponse> createConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody(required = false) AiConversationCreateRequest request) {
        return ResponseEntity.ok(
                aiChatService.createConversation(userDetails.getUsername(), request));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<AiConversationResponse>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                aiChatService.getConversations(userDetails.getUsername()));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<AiMessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer conversationId) {
        return ResponseEntity.ok(aiChatService.getMessages(
                userDetails.getUsername(),
                conversationId));
    }

    @PatchMapping("/conversations/{conversationId}/consent")
    public ResponseEntity<AiConversationResponse> updateConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer conversationId,
            @Valid @RequestBody AiConsentRequest request) {
        return ResponseEntity.ok(aiChatService.updateConsent(
                userDetails.getUsername(),
                conversationId,
                request.getConsent()));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<MessageResponse> deleteConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer conversationId) {
        aiChatService.deleteConversation(userDetails.getUsername(), conversationId);
        return ResponseEntity.ok(new MessageResponse("Đã xóa cuộc trò chuyện."));
    }

    @PostMapping(
            value = "/conversations/{conversationId}/messages",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer conversationId,
            @Valid @RequestBody AiChatRequest request) {
        return aiChatService.streamMessage(
                userDetails.getUsername(),
                conversationId,
                request);
    }
}
