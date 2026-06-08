package datn_gym.controller;

import datn_gym.dto.request.PtNoteRequest;
import datn_gym.dto.response.MessageResponse;
import datn_gym.dto.response.PtNoteResponse;
import datn_gym.service.PtNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pt/notes")
@PreAuthorize("hasRole('PT')")
@RequiredArgsConstructor
public class PtNoteController {

    private final PtNoteService ptNoteService;

    // GET /api/pt/notes
    @GetMapping
    public ResponseEntity<List<PtNoteResponse>> getAllMyNotes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ptNoteService.getAllMyNotes(userDetails.getUsername()));
    }

    // GET /api/pt/notes/member/{memberId}
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PtNoteResponse>> getNotesByMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(
                ptNoteService.getNotesByMember(userDetails.getUsername(), memberId));
    }

    // POST /api/pt/notes
    @PostMapping
    public ResponseEntity<PtNoteResponse> createNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PtNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ptNoteService.createNote(userDetails.getUsername(), request));
    }

    // PUT /api/pt/notes/{noteId}
    @PutMapping("/{noteId}")
    public ResponseEntity<PtNoteResponse> updateNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer noteId,
            @Valid @RequestBody PtNoteRequest request) {
        return ResponseEntity.ok(
                ptNoteService.updateNote(userDetails.getUsername(), noteId, request));
    }

    // DELETE /api/pt/notes/{noteId}
    // Trả 200 + message thay vì 204 No Content để frontend dễ xử lý
    @DeleteMapping("/{noteId}")
    public ResponseEntity<MessageResponse> deleteNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer noteId) {
        ptNoteService.deleteNote(userDetails.getUsername(), noteId);
        return ResponseEntity.ok(new MessageResponse("Xóa ghi chú thành công"));
    }
}