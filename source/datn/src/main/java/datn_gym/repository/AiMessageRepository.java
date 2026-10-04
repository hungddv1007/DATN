package datn_gym.repository;

import datn_gym.entity.AiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiMessageRepository extends JpaRepository<AiMessage, Long> {

    List<AiMessage> findByConversation_IdOrderByCreatedAtAsc(Integer conversationId);

    List<AiMessage> findByConversation_IdOrderByCreatedAtDesc(
            Integer conversationId,
            Pageable pageable);
}
