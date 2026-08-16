package datn_gym.controller;

import datn_gym.dto.request.ChatHandoffRequest;
import datn_gym.dto.request.HumanChatMessageRequest;
import datn_gym.dto.response.AiConversationResponse;
import datn_gym.dto.response.AiMessageResponse;
import datn_gym.service.HumanChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HumanChatController {
    private final HumanChatService service;

    @PostMapping("/api/member/ai/conversations/{id}/handoff")
    public AiConversationResponse handoff(Authentication auth, @PathVariable Integer id,
                                          @Valid @RequestBody ChatHandoffRequest request) {
        return service.requestHandoff(auth.getName(), id);
    }

    @PostMapping("/api/member/ai/conversations/{id}/human-messages")
    public AiMessageResponse memberMessage(Authentication auth, @PathVariable Integer id,
                                           @Valid @RequestBody HumanChatMessageRequest request) {
        return service.sendMemberMessage(auth.getName(), id, request.getMessage());
    }

    @GetMapping("/api/sale/chats")
    public List<AiConversationResponse> saleChats(Authentication auth) {
        return service.saleConversations(auth.getName());
    }

    @PostMapping("/api/sale/chats/claim-next")
    public AiConversationResponse claimNext(Authentication auth) {
        return service.claimOldestWaiting(auth.getName());
    }

    @GetMapping("/api/sale/chats/{id}/messages")
    public List<AiMessageResponse> saleMessages(Authentication auth, @PathVariable Integer id) {
        return service.saleMessages(auth.getName(), id);
    }

    @PostMapping("/api/sale/chats/{id}/messages")
    public AiMessageResponse saleMessage(Authentication auth, @PathVariable Integer id,
                                         @Valid @RequestBody HumanChatMessageRequest request) {
        return service.sendSaleMessage(auth.getName(), id, request.getMessage());
    }

    @PostMapping("/api/sale/chats/{id}/close")
    public AiConversationResponse close(Authentication auth, @PathVariable Integer id) {
        return service.close(auth.getName(), id);
    }
}
