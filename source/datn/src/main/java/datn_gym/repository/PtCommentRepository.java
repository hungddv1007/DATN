package datn_gym.repository;

import datn_gym.entity.PtComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PtCommentRepository extends JpaRepository<PtComment, Integer> {
    List<PtComment> findByMember_IdOrderByCreatedAtDesc(Integer memberId);
    List<PtComment> findByMember_IdAndRoute_IdOrderByCreatedAtDesc(Integer memberId, Integer routeId);
    List<PtComment> findByPt_IdOrderByCreatedAtDesc(Integer ptId);
    List<PtComment> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);
}