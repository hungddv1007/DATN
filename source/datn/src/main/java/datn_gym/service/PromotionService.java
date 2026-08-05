package datn_gym.service;

import datn_gym.dto.request.PromotionRequest;
import datn_gym.dto.response.PromotionResponse;
import datn_gym.entity.GymPackage;
import datn_gym.entity.Promotion;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final GymPackageRepository gymPackageRepository;

    // ----------------------------------------------------------------
    // Lấy tất cả khuyến mãi (dành cho Admin)
    // ----------------------------------------------------------------
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // Thêm mới khuyến mãi
    // ----------------------------------------------------------------
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã khuyến mãi đã tồn tại");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc không được trước ngày bắt đầu");
        }

        Promotion promotion = new Promotion();
        promotion.setCode(request.getCode().toUpperCase());
        promotion.setDiscountPercent(request.getDiscountPercent());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMaxUsage(request.getMaxUsage());
        promotion.setCurrentUsage(0);
        promotion.setIsActive(true);

        if (request.getPackageId() != null) {
            GymPackage pkg = gymPackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
            promotion.setGymPackage(pkg);
        }

        return toResponse(promotionRepository.save(promotion));
    }

    // ----------------------------------------------------------------
    // Sửa khuyến mãi
    // ----------------------------------------------------------------
    @Transactional
    public PromotionResponse updatePromotion(Integer id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));

        if (!promotion.getCode().equalsIgnoreCase(request.getCode()) && promotionRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã khuyến mãi đã tồn tại");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc không được trước ngày bắt đầu");
        }

        promotion.setCode(request.getCode().toUpperCase());
        promotion.setDiscountPercent(request.getDiscountPercent());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMaxUsage(request.getMaxUsage());

        if (request.getPackageId() != null) {
            GymPackage pkg = gymPackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
            promotion.setGymPackage(pkg);
        } else {
            promotion.setGymPackage(null);
        }

        return toResponse(promotionRepository.save(promotion));
    }

    // ----------------------------------------------------------------
    // Ẩn/Hiện khuyến mãi
    // ----------------------------------------------------------------
    @Transactional
    public PromotionResponse togglePromotionStatus(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));
        
        Boolean currentStatus = promotion.getIsActive() != null ? promotion.getIsActive() : true;
        promotion.setIsActive(!currentStatus);
        return toResponse(promotionRepository.save(promotion));
    }

    // ----------------------------------------------------------------
    // Xóa vĩnh viễn
    // ----------------------------------------------------------------
    @Transactional
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));
        // Nếu đã có người xài thì không nên cho xóa, thay vào đó là ẩn.
        if (promotion.getCurrentUsage() != null && promotion.getCurrentUsage() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khuyến mãi này đã có người sử dụng, không thể xóa. Hãy dùng chức năng Ẩn.");
        }
        try {
            promotionRepository.delete(promotion);
            promotionRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Khuyến mãi này đã từng được dùng trong giao dịch nên không thể xóa. Hãy dùng chức năng Ẩn.");
        }
    }

    // ----------------------------------------------------------------
    // HELPER: Convert sang DTO
    // ----------------------------------------------------------------
    private PromotionResponse toResponse(Promotion p) {
        LocalDate now = LocalDate.now();
        String status = "Đang diễn ra";
        
        Boolean isActive = p.getIsActive() != null ? p.getIsActive() : true;
        
        if (!isActive) {
            status = "Đã ẩn";
        } else if (now.isBefore(p.getStartDate())) {
            status = "Sắp diễn ra";
        } else if (now.isAfter(p.getEndDate())) {
            status = "Đã kết thúc";
        } else if (p.getMaxUsage() != null && p.getCurrentUsage() != null && p.getCurrentUsage() >= p.getMaxUsage()) {
            status = "Hết lượt";
        }

        return PromotionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .discountPercent(p.getDiscountPercent())
                .packageId(p.getGymPackage() != null ? p.getGymPackage().getId() : null)
                .packageName(p.getGymPackage() != null ? p.getGymPackage().getName() : "Tất cả gói")
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .maxUsage(p.getMaxUsage())
                .currentUsage(p.getCurrentUsage())
                .isActive(isActive)
                .status(status)
                .build();
    }
}
