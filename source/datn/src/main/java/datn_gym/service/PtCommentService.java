package datn_gym.service;

import datn_gym.dto.request.PtCommentCreateRequest;
import datn_gym.dto.request.PtCommentUpdateRequest;
import datn_gym.dto.response.PtCommentResponse;
import datn_gym.entity.PtComment;
import datn_gym.entity.TrainingRoute;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtCommentRepository;
import datn_gym.repository.TrainingRouteRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PtCommentService {

    private final PtCommentRepository ptCommentRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final TrainingRouteRepository trainingRouteRepository;

    // ----------------------------------------------------------------
    // PT: Xem tất cả nhận xét mình đã gửi
    // ----------------------------------------------------------------
    public List<PtCommentResponse> getAllMyComments(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        return ptCommentRepository.findByPt_IdOrderByCreatedAtDesc(pt.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PT: Xem nhận xét mình đã gửi cho một hội viên cụ thể
    // ----------------------------------------------------------------
    public List<PtCommentResponse> getCommentsByMember(String ptEmail, Integer memberId) {
        User pt = getUserByEmail(ptEmail);
        validatePtOwnsMember(pt.getId(), memberId);
        return ptCommentRepository
                .findByPt_IdAndMember_IdOrderByCreatedAtDesc(pt.getId(), memberId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PT: Gửi nhận xét mới
    // ----------------------------------------------------------------
    @Transactional
    public PtCommentResponse createComment(String ptEmail, PtCommentCreateRequest request) {
        User pt = getUserByEmail(ptEmail);

        // FIX Lỗi 4: Validate TRƯỚC khi load member — tránh query thừa nếu fail
        validatePtOwnsMember(pt.getId(), request.getMemberId());

        User member = getUserById(request.getMemberId());

        // Xử lý routeId (optional)
        TrainingRoute route = null;
        if (request.getRouteId() != null) {
            route = trainingRouteRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

            // FIX Lỗi 3: Phân biệt rõ 2 trường hợp route không hợp lệ
            if (route.getMember() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Lộ trình này chưa được giao cho hội viên nào (là template)");
            }
            if (!route.getMember().getId().equals(member.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Lộ trình này không thuộc về hội viên đã chọn");
            }
        }

        PtComment comment = PtComment.builder()
                .pt(pt).member(member).route(route)
                .content(request.getContent()).build();
        return toResponse(ptCommentRepository.save(comment));
    }

    // ----------------------------------------------------------------
    // PT: Sửa nhận xét — chỉ update content
    // ----------------------------------------------------------------
    @Transactional
    public PtCommentResponse updateComment(String ptEmail, Integer commentId,
                                           PtCommentUpdateRequest request) {
        User pt = getUserByEmail(ptEmail);

        // FIX Lỗi 2: Tách 2 bước — phân biệt 404 vs 403
        if (!ptCommentRepository.existsById(commentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy nhận xét");
        }

        PtComment comment = ptCommentRepository.findByIdAndPt_Id(commentId, pt.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Bạn không có quyền sửa nhận xét này"));

        comment.setContent(request.getContent());
        return toResponse(ptCommentRepository.save(comment));
    }

    // ----------------------------------------------------------------
    // PT: Xóa nhận xét
    // ----------------------------------------------------------------
    @Transactional
    public void deleteComment(String ptEmail, Integer commentId) {
        User pt = getUserByEmail(ptEmail);

        // FIX Lỗi 2: Tách 2 bước — phân biệt 404 vs 403
        if (!ptCommentRepository.existsById(commentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy nhận xét");
        }

        PtComment comment = ptCommentRepository.findByIdAndPt_Id(commentId, pt.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Bạn không có quyền xóa nhận xét này"));

        ptCommentRepository.delete(comment);
    }

    // ----------------------------------------------------------------
    // MEMBER: Xem tất cả nhận xét được gửi cho mình
    // ----------------------------------------------------------------
    public List<PtCommentResponse> getMyComments(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        return ptCommentRepository.findByMember_IdOrderByCreatedAtDesc(member.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // MEMBER: Xem nhận xét gắn với một lộ trình cụ thể
    // ----------------------------------------------------------------
    public List<PtCommentResponse> getMyCommentsByRoute(String memberEmail, Integer routeId) {
        User member = getUserByEmail(memberEmail);

        TrainingRoute route = trainingRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

        // FIX Lỗi 3: Phân biệt template vs không có quyền
        if (route.getMember() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình");
        }
        if (!route.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền xem lộ trình này");
        }

        return ptCommentRepository
                .findByMember_IdAndRoute_IdOrderByCreatedAtDesc(member.getId(), routeId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // HELPER: Check tại DB — member có đang ACTIVE với PT này không
    // ----------------------------------------------------------------
    private void validatePtOwnsMember(Integer ptId, Integer memberId) {
        if (!membershipRepository.existsActiveMembershipByPtAndMember(ptId, memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Hội viên này không thuộc quyền quản lý của bạn");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hội viên với id: " + id));
    }

    // FIX N+1: toResponse() chỉ đọc field — không trigger thêm query
    // vì @EntityGraph đã load sẵn pt, member, route trong 1 câu SQL
    private PtCommentResponse toResponse(PtComment comment) {
        return PtCommentResponse.builder()
                .id(comment.getId())
                .ptId(comment.getPt().getId())
                .ptName(comment.getPt().getFullName())
                .ptAvatar(comment.getPt().getAvatar())
                .memberId(comment.getMember().getId())
                .memberName(comment.getMember().getFullName())
                .routeId(comment.getRoute() != null ? comment.getRoute().getId() : null)
                .routeName(comment.getRoute() != null ? comment.getRoute().getName() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}