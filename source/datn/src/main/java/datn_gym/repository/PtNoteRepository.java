package datn_gym.repository;

import datn_gym.entity.PtNote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PtNoteRepository extends JpaRepository<PtNote, Integer> {

    // FIX N+1: Load pt + member trong 1 câu SQL (đồng bộ pattern PtCommentRepository)
    @EntityGraph(attributePaths = {"pt", "member"})
    List<PtNote> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);

    @EntityGraph(attributePaths = {"pt", "member"})
    List<PtNote> findByPt_IdOrderByCreatedAtDesc(Integer ptId);

    // FIX IDOR + Truy vấn dư thừa:
    // Check ownership tại DB trong 1 câu SQL thay vì lấy lên rồi check bằng Java
    Optional<PtNote> findByIdAndPt_Id(Integer id, Integer ptId);

    // FIX KIẾN TRÚC: Đã XÓA query "FROM Membership" khỏi đây
    // PtNoteRepository chỉ nên query bảng pt_notes — không "cầm nhầm" bảng khác
    // Logic check membership đã chuyển hẳn về MembershipRepository
    // (Service sẽ inject MembershipRepository thay vì gọi qua đây)
}