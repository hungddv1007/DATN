package datn_gym.controller;

import datn_gym.dto.request.DietCreateRequest;
import datn_gym.dto.request.DietUpdateRequest;
import datn_gym.dto.response.DietResponse;
import datn_gym.dto.response.MessageResponse;
import datn_gym.service.DietService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    // ================================================================
    // PT APIs
    // ================================================================

    // GET /api/pt/diets/member/{memberId}
    // PT xem tất cả diet đã tạo cho 1 HV (mẫu + ngày cụ thể)
    @GetMapping("/api/pt/diets/member/{memberId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<List<DietResponse>> getDietsByMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId) {
        return ResponseEntity.ok(
                dietService.getDietsByMember(userDetails.getUsername(), memberId));
    }

    // GET /api/pt/diets/member/{memberId}/template/{dayType}
    // PT xem mẫu TRAINING_DAY hoặc REST_DAY
    @GetMapping("/api/pt/diets/member/{memberId}/template/{dayType}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<DietResponse> getDietTemplate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer memberId,
            @PathVariable String dayType) {
        return ResponseEntity.ok(
                dietService.getDietTemplate(
                        userDetails.getUsername(), memberId, dayType));
    }

    // POST /api/pt/diets
    // PT tạo thực đơn (mẫu TRAINING_DAY/REST_DAY hoặc SPECIFIC_DATE)
    @PostMapping("/api/pt/diets")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<DietResponse> createDiet(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DietCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dietService.createDiet(userDetails.getUsername(), request));
    }

    // PUT /api/pt/diets/{dietId}
    // PT sửa thực đơn
    @PutMapping("/api/pt/diets/{dietId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<DietResponse> updateDiet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer dietId,
            @Valid @RequestBody DietUpdateRequest request) {
        return ResponseEntity.ok(
                dietService.updateDiet(userDetails.getUsername(), dietId, request));
    }

    // DELETE /api/pt/diets/{dietId}
    // PT xóa thực đơn
    @DeleteMapping("/api/pt/diets/{dietId}")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<MessageResponse> deleteDiet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer dietId) {
        dietService.deleteDiet(userDetails.getUsername(), dietId);
        return ResponseEntity.ok(new MessageResponse("Xóa thực đơn thành công"));
    }

    // ================================================================
    // MEMBER APIs
    // ================================================================

    // GET /api/member/diets
    // Member xem toàn bộ diet (mẫu + ngày cụ thể)
    @GetMapping("/api/member/diets")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<DietResponse>> getMyDiets(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                dietService.getMyDiets(userDetails.getUsername()));
    }

    // GET /api/member/diets/date/{date}
    // Member xem thực đơn ngày cụ thể — AUTO-MAPPING
    @GetMapping("/api/member/diets/date/{date}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<DietResponse> getMyDietForDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                dietService.getMyDietForDate(userDetails.getUsername(), date));
    }

    // GET /api/member/diets/week?from=2025-07-01&to=2025-07-07
    // Member xem thực đơn tuần — AUTO-MAPPING cho từng ngày
    @GetMapping("/api/member/diets/week")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<DietResponse>> getMyDietWeekView(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(
                dietService.getMyDietWeekView(userDetails.getUsername(), from, to));
    }
}
