package datn_gym.service;

import datn_gym.dto.request.PtNoteRequest;
import datn_gym.dto.response.PtNoteResponse;
import datn_gym.entity.PtNote;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtNoteRepository;
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
public class PtNoteService {

    private final PtNoteRepository ptNoteRepository;
    private final UserRepository userRepository;

    // FIX KIẾN TRÚC: Inject đúng MembershipRepository
    // (Bản trước gọi sai qua ptNoteRepository.existsActiveMembershipByPtAndMember
    //  -> method này không tồn tại trong PtNoteRepository -> lỗi compile)
    private final MembershipRepository membershipRepository;

    // ----------------------------------------------------------------
    // PT: Lấy tất cả ghi chú của mình (toàn bộ hội viên)
    // ----------------------------------------------------------------
    public List<PtNoteResponse> getAllMyNotes(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        return ptNoteRepository
                .findByPt_IdOrderByCreatedAtDesc(pt.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PT: Lấy ghi chú về một hội viên cụ thể
    // ----------------------------------------------------------------
    public List<PtNoteResponse> getNotesByMember(String ptEmail, Integer memberId) {
        User pt = getUserByEmail(ptEmail);

        // FIX IDOR: Check tại DB qua MembershipRepository — member có thuộc PT này không
        validatePtOwnsMember(pt.getId(), memberId);

        return ptNoteRepository
                .findByPt_IdAndMember_IdOrderByCreatedAtDesc(pt.getId(), memberId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PT: Tạo ghi chú mới
    // ----------------------------------------------------------------
    @Transactional
    public PtNoteResponse createNote(String ptEmail, PtNoteRequest request) {
        User pt = getUserByEmail(ptEmail);

        // FIX IDOR: Validate TRƯỚC khi load member — tránh query thừa nếu fail
        // Ngăn PT ghi chú cho Admin hoặc hội viên của PT khác
        validatePtOwnsMember(pt.getId(), request.getMemberId());

        User member = getUserById(request.getMemberId());

        PtNote note = PtNote.builder()
                .pt(pt)
                .member(member)
                .content(request.getContent())
                .build();

        return toResponse(ptNoteRepository.save(note));
    }

    // ----------------------------------------------------------------
    // PT: Sửa ghi chú
    // ----------------------------------------------------------------
    @Transactional
    public PtNoteResponse updateNote(String ptEmail, Integer noteId, PtNoteRequest request) {
        User pt = getUserByEmail(ptEmail);

        // FIX Lỗi 404 vs 403: Tách 2 bước để phân biệt rõ
        if (!ptNoteRepository.existsById(noteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy ghi chú");
        }

        // FIX IDOR + Truy vấn dư thừa:
        // findByIdAndPt_Id → 1 câu SQL vừa tìm vừa check ownership tại DB
        PtNote note = ptNoteRepository.findByIdAndPt_Id(noteId, pt.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn không có quyền sửa ghi chú này"));

        note.setContent(request.getContent());
        return toResponse(ptNoteRepository.save(note));
    }

    // ----------------------------------------------------------------
    // PT: Xóa ghi chú
    // ----------------------------------------------------------------
    @Transactional
    public void deleteNote(String ptEmail, Integer noteId) {
        User pt = getUserByEmail(ptEmail);

        // FIX Lỗi 404 vs 403: Tách 2 bước để phân biệt rõ
        if (!ptNoteRepository.existsById(noteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy ghi chú");
        }

        PtNote note = ptNoteRepository.findByIdAndPt_Id(noteId, pt.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn không có quyền xóa ghi chú này"));

        ptNoteRepository.delete(note);
    }

    // ----------------------------------------------------------------
    // HELPER: Check tại DB — member có đang ACTIVE với PT này không
    // FIX KIẾN TRÚC: Gọi đúng membershipRepository, không "cầm nhầm" PtNoteRepository
    // ----------------------------------------------------------------
    private void validatePtOwnsMember(Integer ptId, Integer memberId) {
        boolean isAssigned = membershipRepository
                .existsActiveMembershipByPtAndMember(ptId, memberId);

        if (!isAssigned) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hội viên này không thuộc quyền quản lý của bạn");
        }
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy User theo email
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy User theo ID
    // ----------------------------------------------------------------
    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hội viên với id: " + id));
    }

    // ----------------------------------------------------------------
    // HELPER: Chuyển Entity → Response DTO
    // FIX N+1: @EntityGraph trong Repository đã load sẵn pt, member
    // ----------------------------------------------------------------
    private PtNoteResponse toResponse(PtNote note) {
        return PtNoteResponse.builder()
                .id(note.getId())
                .ptId(note.getPt().getId())
                .ptName(note.getPt().getFullName())
                .memberId(note.getMember().getId())
                .memberName(note.getMember().getFullName())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .build();
    }
}