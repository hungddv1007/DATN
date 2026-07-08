package datn_gym.controller;

import datn_gym.dto.request.PackageDiscountRequest;
import datn_gym.dto.response.MessageResponse;
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

    // PUBLIC: Lấy danh sách chiết khấu để hiển thị trên UI
    @GetMapping("/api/public/discounts")
    public ResponseEntity<List<PackageDiscountResponse>> getPublicDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // ADMIN: Lấy tất cả
    @GetMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PackageDiscountResponse>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // ADMIN: Thêm mới
    @PostMapping("/api/admin/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDiscountResponse> createDiscount(
            @Valid @RequestBody PackageDiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountService.createDiscount(request));
    }

    // ADMIN: Cập nhật
    @PutMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDiscountResponse> updateDiscount(
            @PathVariable Integer id,
            @Valid @RequestBody PackageDiscountRequest request) {
        return ResponseEntity.ok(discountService.updateDiscount(id, request));
    }

    // ADMIN: Xoá
    @DeleteMapping("/api/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(new MessageResponse("Xóa chiết khấu thành công."));
    }
}
