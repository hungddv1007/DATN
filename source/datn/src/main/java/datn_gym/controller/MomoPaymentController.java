package datn_gym.controller;

import datn_gym.dto.request.MomoIpnRequest;
import datn_gym.dto.response.MomoPaymentResponse;
import datn_gym.service.MomoPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MomoPaymentController {

    private final MomoPaymentService momoPaymentService;

    @PostMapping("/api/member/payments/momo/{transactionId}")
    public ResponseEntity<MomoPaymentResponse> initiate(
            @PathVariable Integer transactionId,
            Authentication authentication) {
        return ResponseEntity.ok(momoPaymentService.initiate(
                transactionId, authentication.getName()));
    }

    @GetMapping("/api/member/payments/momo/{transactionId}")
    public ResponseEntity<MomoPaymentResponse> getStatus(
            @PathVariable Integer transactionId,
            Authentication authentication) {
        return ResponseEntity.ok(momoPaymentService.getStatus(
                transactionId, authentication.getName()));
    }

    @PostMapping("/api/member/payments/momo/{transactionId}/refresh")
    public ResponseEntity<MomoPaymentResponse> refresh(
            @PathVariable Integer transactionId,
            Authentication authentication) {
        return ResponseEntity.ok(momoPaymentService.refreshStatus(
                transactionId, authentication.getName()));
    }

    @PostMapping("/api/member/payments/momo/{transactionId}/return")
    public ResponseEntity<MomoPaymentResponse> handleReturn(
            @PathVariable Integer transactionId,
            @RequestBody MomoIpnRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(momoPaymentService.handleReturn(
                transactionId, authentication.getName(), request));
    }

    @DeleteMapping("/api/member/payments/momo/{transactionId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Integer transactionId,
            Authentication authentication) {
        momoPaymentService.cancel(transactionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/public/payments/momo/ipn")
    public ResponseEntity<Void> ipn(@RequestBody MomoIpnRequest request) {
        momoPaymentService.handleIpn(request);
        return ResponseEntity.noContent().build();
    }
}
