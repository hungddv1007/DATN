package datn_gym.controller;

import datn_gym.dto.request.CreateSaleAccountRequest;
import datn_gym.dto.response.CommissionResponse;
import datn_gym.dto.response.UserProfileResponse;
import datn_gym.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sales")
@RequiredArgsConstructor
public class AdminSaleController {
    private final SaleService service;
    @GetMapping public ResponseEntity<List<UserProfileResponse>> list() { return ResponseEntity.ok(service.getSaleAccounts()); }
    @PostMapping("/accounts") public ResponseEntity<UserProfileResponse> create(@Valid @RequestBody CreateSaleAccountRequest r) { return ResponseEntity.ok(service.createSaleAccount(r)); }
    @GetMapping("/commissions") public ResponseEntity<List<CommissionResponse>> commissions() { return ResponseEntity.ok(service.getAllCommissions()); }
    @PutMapping("/commissions/{id}/paid") public ResponseEntity<CommissionResponse> paid(@PathVariable Long id) { return ResponseEntity.ok(service.markCommissionPaid(id)); }
}
