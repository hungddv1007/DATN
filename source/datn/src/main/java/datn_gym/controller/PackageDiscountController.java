package datn_gym.controller;

import datn_gym.service.PackageDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PackageDiscountController {

    private final PackageDiscountService discountService;

    // ================================================================
    // PUBLIC: Lấy danh sách chiết khấu (frontend hiển thị cho member xem)
    // ================================================================
    @GetMapping("/api/public/discounts")
    public ResponseEntity<List<Map<String, Object>>> getPublicDiscounts() {
        return ResponseEntity.ok(discountService.getAll());
    }

    // ================================================================
    // ADMIN: CRUD chiết khấu
    // ================================================================
    @GetMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(discountService.getAll());
    }

    @PostMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Integer packageId = body.get("packageId") != null ? (Integer) body.get("packageId") : null;
        int minDays = (Integer) body.get("minDays");
        int discountPercent = (Integer) body.get("discountPercent");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountService.create(packageId, minDays, discountPercent));
    }

    @PutMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        Integer packageId = body.get("packageId") != null ? (Integer) body.get("packageId") : null;
        int minDays = (Integer) body.get("minDays");
        int discountPercent = (Integer) body.get("discountPercent");
        return ResponseEntity.ok(discountService.update(id, packageId, minDays, discountPercent));
    }

    @DeleteMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        discountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
