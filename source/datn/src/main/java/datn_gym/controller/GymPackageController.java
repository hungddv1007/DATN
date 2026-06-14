package datn_gym.controller;

import datn_gym.dto.request.GymPackageRequest;
import datn_gym.dto.response.GymPackageResponse;
import datn_gym.service.GymPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class GymPackageController {

    private final GymPackageService gymPackageService;

    // GET /api/packages — Danh sách tất cả gói tập (công khai)
    @GetMapping
    public ResponseEntity<List<GymPackageResponse>> getAllPackages(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        // Mặc định public gọi sẽ truyền activeOnly = true, còn Admin sẽ truyền activeOnly = false
        return ResponseEntity.ok(gymPackageService.getAllPackages(!activeOnly));
    }

    // GET /api/packages/{id} — Chi tiết gói tập (công khai)
    @GetMapping("/{id}")
    public ResponseEntity<GymPackageResponse> getPackageById(@PathVariable Integer id) {
        return ResponseEntity.ok(gymPackageService.getPackageById(id));
    }

    // POST /api/packages — Tạo gói tập mới (Admin gọi, SecurityConfig chặn theo /api/admin/**)
    // Note: Dùng /api/admin/packages cho Admin CRUD, nhưng GET công khai dùng /api/packages
    @PostMapping
    public ResponseEntity<GymPackageResponse> createPackage(
            @Valid @RequestBody GymPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gymPackageService.createPackage(request));
    }

    // PUT /api/packages/{id} — Cập nhật gói tập (cần auth)
    @PutMapping("/{id}")
    public ResponseEntity<GymPackageResponse> updatePackage(
            @PathVariable Integer id,
            @Valid @RequestBody GymPackageRequest request) {
        return ResponseEntity.ok(gymPackageService.updatePackage(id, request));
    }

    // PUT /api/packages/{id}/toggle-status — Ẩn/hiện gói tập (Admin gọi)
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<GymPackageResponse> togglePackageStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(gymPackageService.togglePackageStatus(id));
    }

    // DELETE /api/packages/{id} — Xóa gói tập (cần auth)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Integer id) {
        gymPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
