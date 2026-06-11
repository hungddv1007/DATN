package datn_gym.repository;

import datn_gym.entity.PtComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PtCommentRepository extends JpaRepository<PtComment, Integer> {

    @EntityGraph(attributePaths = { "pt", "member", "route" })
    List<PtComment> findByPt_IdOrderByCreatedAtDesc(Integer ptId);

    @EntityGraph(attributePaths = { "pt", "member", "route" })
    List<PtComment> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);

    @EntityGraph(attributePaths = { "pt", "member", "route" })
    List<PtComment> findByMember_IdOrderByCreatedAtDesc(Integer memberId);

    @EntityGraph(attributePaths = { "pt", "member", "route" })
    List<PtComment> findByMember_IdAndRoute_IdOrderByCreatedAtDesc(Integer memberId, Integer routeId);

    // Dùng để check ownership khi update/delete — không cần load pt/member/route
    Optional<PtComment> findByIdAndPt_Id(Integer id, Integer ptId);

    // FIX Lỗi 2: Tìm theo id đơn thuần để phân biệt "không tồn tại" vs "không có
    // quyền"
    boolean existsById(Integer id);
}