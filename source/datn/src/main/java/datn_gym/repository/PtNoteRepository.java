package datn_gym.repository;

import datn_gym.entity.PtNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PtNoteRepository extends JpaRepository<PtNote, Integer> {
    List<PtNote> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);

    List<PtNote> findByPt_IdOrderByCreatedAtDesc(Integer ptId);

    // FIX IDOR + Truy vấn dư thừa:
    // Check ownership tại DB trong 1 câu SQL thay vì lấy lên rồi check bằng Java
    Optional<PtNote> findByIdAndPt_Id(Integer id, Integer ptId);
}