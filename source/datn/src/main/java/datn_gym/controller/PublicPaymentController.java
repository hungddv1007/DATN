package datn_gym.controller;

import datn_gym.config.PaymentProperties;
import datn_gym.dto.response.PaymentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/payment-info")
@RequiredArgsConstructor
public class PublicPaymentController {

    private final PaymentProperties properties;

    @GetMapping
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo() {
        return ResponseEntity.ok(new PaymentInfoResponse(
                safe(properties.bankName()),
                safe(properties.bankAccountNumber()),
                safe(properties.bankAccountHolder()),
                safe(properties.transferPrefix()),
                properties.effectivePendingExpirationHours()));
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
