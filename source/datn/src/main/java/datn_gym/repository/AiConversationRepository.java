package datn_gym.repository;

import datn_gym.entity.AiConversation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiConversationRepository extends JpaRepository<AiConversation, Integer> {

    @EntityGraph(attributePaths = "user")
    List<AiConversation> findByUser_IdOrderByUpdatedAtDesc(Integer userId);

    @EntityGraph(attributePaths = "user")
    Optional<AiConversation> findByIdAndUser_Id(Integer id, Integer userId);

    List<AiConversation> findByUpdatedAtBefore(LocalDateTime cutoff);

    List<AiConversation> findByAssignedSale_IdAndHandoffStatusInOrderByUpdatedAtDesc(
            Integer saleId, List<String> statuses);

    long countByAssignedSale_IdAndHandoffStatusIn(Integer saleId, List<String> statuses);

    List<AiConversation> findByHandoffStatusOrderByHandoffAtAsc(String status);
}
