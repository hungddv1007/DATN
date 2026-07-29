package datn_gym.controller;

import datn_gym.dto.request.PackageDiscountRequest;
import datn_gym.dto.response.PackageDiscountResponse;
import datn_gym.service.PackageDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PackageDiscountController {

    private final PackageDiscountService discountService;

    // ================================================================
    // PUBLIC: Lấy danh sách chiết khấu (frontend hiển thị cho member xem)
    // ================================================================
    @GetMapping("/api/public/discounts")
    public ResponseEntity<List<PackageDiscountResponse>> getPublicDiscounts() {
        return ResponseEntity.ok(discountService.getAll());
    }

    // ================================================================
    // ADMIN: CRUD chiết khấu
    // ================================================================
    @GetMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PackageDiscountResponse>> getAll() {
        return ResponseEntity.ok(discountService.getAll());
    }

    @PostMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDiscountResponse> create(@Valid @RequestBody PackageDiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountService.create(request.getPackageId(), request.getMinDays(), request.getDiscountPercent()));
    }

    @PutMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDiscountResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody PackageDiscountRequest request) {
        return ResponseEntity.ok(discountService.update(id, request.getPackageId(), request.getMinDays(), request.getDiscountPercent()));
    }

    @DeleteMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        discountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
