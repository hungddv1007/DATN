package datn_gym.controller;

import datn_gym.dto.request.SaleCodeRequest;
import datn_gym.dto.response.*;
import datn_gym.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService service;

    @GetMapping("/dashboard") public ResponseEntity<SaleDashboardResponse> dashboard(Authentication a) { return ResponseEntity.ok(service.dashboard(a.getName())); }
    @PutMapping("/availability") public ResponseEntity<SaleDashboardResponse> availability(Authentication a, @RequestParam boolean online) { return ResponseEntity.ok(service.setOnline(a.getName(), online)); }
    @GetMapping("/codes") public ResponseEntity<List<SaleCodeResponse>> codes(Authentication a) { return ResponseEntity.ok(service.getCodes(a.getName())); }
    @PutMapping("/codes/{id}") public ResponseEntity<SaleCodeResponse> updateCode(Authentication a, @PathVariable Integer id, @Valid @RequestBody SaleCodeRequest r) { return ResponseEntity.ok(service.updateCode(a.getName(), id, r)); }
    @PutMapping("/codes/{id}/active") public ResponseEntity<SaleCodeResponse> active(Authentication a, @PathVariable Integer id, @RequestParam boolean value) { return ResponseEntity.ok(service.setCodeActive(a.getName(), id, value)); }
    @GetMapping("/commissions") public ResponseEntity<List<CommissionResponse>> commissions(Authentication a) { return ResponseEntity.ok(service.getCommissions(a.getName())); }
}
