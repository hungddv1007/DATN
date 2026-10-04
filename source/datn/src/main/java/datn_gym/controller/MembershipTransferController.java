package datn_gym.controller;

import datn_gym.dto.request.*;
import datn_gym.dto.response.MembershipTransferResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.MembershipTransferService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/membership-transfers")
@RequiredArgsConstructor
public class MembershipTransferController {
    private final MembershipTransferService service;

    @PostMapping("/verify-sender")
    public MessageResponse verifySender(Authentication auth,
                                        @Valid @RequestBody TransferVerificationRequest request) {
        service.verifySenderAndSendOtp(auth.getName(), request);
        return new MessageResponse("Đã xác thực. Mã OTP đã được gửi đến email của bạn.");
    }

    @PostMapping
    public ResponseEntity<MembershipTransferResponse> create(
            Authentication auth, @Valid @RequestBody MembershipTransferCreateRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(auth.getName(), request,
                clientIp(httpRequest), httpRequest.getHeader("User-Agent")));
    }

    @PostMapping("/{id}/send-accept-otp")
    public MessageResponse sendAcceptOtp(Authentication auth, @PathVariable Long id) {
        service.sendRecipientOtp(auth.getName(), id);
        return new MessageResponse("Mã OTP xác nhận nhận gói đã được gửi đến email của bạn.");
    }

    @PostMapping("/{id}/accept")
    public MembershipTransferResponse accept(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody MembershipTransferAcceptRequest request,
            HttpServletRequest httpRequest) {
        return service.accept(auth.getName(), id, request,
                clientIp(httpRequest), httpRequest.getHeader("User-Agent"));
    }

    @PostMapping("/{id}/reject")
    public MembershipTransferResponse reject(Authentication auth, @PathVariable Long id) {
        return service.reject(auth.getName(), id);
    }

    @PostMapping("/{id}/cancel")
    public MembershipTransferResponse cancel(Authentication auth, @PathVariable Long id) {
        return service.cancel(auth.getName(), id);
    }

    @GetMapping("/incoming")
    public List<MembershipTransferResponse> incoming(Authentication auth) {
        return service.incoming(auth.getName());
    }

    @GetMapping("/outgoing")
    public List<MembershipTransferResponse> outgoing(Authentication auth) {
        return service.outgoing(auth.getName());
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded == null || forwarded.isBlank()
                ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
    }
}
