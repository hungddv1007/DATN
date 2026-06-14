package datn_gym.controller;

import datn_gym.dto.response.TransactionResponse;
import datn_gym.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // GET /api/admin/transactions — Tất cả giao dịch (phân trang)
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }

    // GET /api/admin/transactions/pending — Giao dịch đang chờ duyệt
    @GetMapping("/pending")
    public ResponseEntity<Page<TransactionResponse>> getPendingTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionService.getPendingTransactions(pageable));
    }

    // PUT /api/admin/transactions/{id}/confirm — Duyệt giao dịch
    @PutMapping("/{id}/confirm")
    public ResponseEntity<TransactionResponse> confirmTransaction(
            @PathVariable Integer id,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.confirmTransaction(id, adminEmail));
    }

    // PUT /api/admin/transactions/{id}/cancel — Hủy giao dịch
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @PathVariable Integer id,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.cancelTransaction(id, adminEmail));
    }
}
