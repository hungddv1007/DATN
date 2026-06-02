package datn_gym.repository;

import datn_gym.entity.PtNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PtNoteRepository extends JpaRepository<PtNote, Integer> {
    List<PtNote> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);
    List<PtNote> findByPt_IdOrderByCreatedAtDesc(Integer ptId);
}