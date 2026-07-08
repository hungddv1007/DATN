package datn_gym.service;

import datn_gym.dto.request.GymPackageRequest;
import datn_gym.dto.response.GymPackageResponse;
import datn_gym.entity.GymPackage;
import datn_gym.repository.GymPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymPackageService {

    private final GymPackageRepository gymPackageRepository;

    // ----------------------------------------------------------------
    // PUBLIC: Lấy danh sách tất cả gói tập (trang công khai)
    // ----------------------------------------------------------------
    public List<GymPackageResponse> getAllPackages(boolean showAll) {
        List<GymPackage> packages = showAll
            ? gymPackageRepository.findAll()
            : gymPackageRepository.findByIsActiveTrue();

        return packages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PUBLIC: Lấy chi tiết một gói tập
    // ----------------------------------------------------------------
    public GymPackageResponse getPackageById(Integer id) {
        GymPackage pkg = gymPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy gói tập với id: " + id));
        return toResponse(pkg);
    }

    // ----------------------------------------------------------------
    // ADMIN: Tạo gói tập mới
    // ----------------------------------------------------------------
    @Transactional
    public GymPackageResponse createPackage(GymPackageRequest request) {
        GymPackage pkg = GymPackage.builder()
                .name(request.getName())
                .dailyPrice(request.getDailyPrice())
                .description(request.getDescription())
                .minDays(request.getMinDays() != null ? request.getMinDays() : 1)
                .hasPt(request.getHasPt() != null ? request.getHasPt() : false)
                .canChoosePt(request.getCanChoosePt() != null ? request.getCanChoosePt() : false)
                .hasMealPlan(request.getHasMealPlan() != null ? request.getHasMealPlan() : false)
                .maxHoldTimes(request.getMaxHoldTimes() != null ? request.getMaxHoldTimes() : 0)
                .holdReturnPercent(request.getHoldReturnPercent() != null ? request.getHoldReturnPercent() : 0)
                .build();

        return toResponse(gymPackageRepository.save(pkg));
    }

    // ----------------------------------------------------------------
    // ADMIN: Cập nhật gói tập
    // ----------------------------------------------------------------
    @Transactional
    public GymPackageResponse updatePackage(Integer id, GymPackageRequest request) {
        GymPackage pkg = gymPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy gói tập với id: " + id));

        pkg.setName(request.getName());
        pkg.setDailyPrice(request.getDailyPrice());
        pkg.setDescription(request.getDescription());
        pkg.setMinDays(request.getMinDays() != null ? request.getMinDays() : 1);
        pkg.setHasPt(request.getHasPt() != null ? request.getHasPt() : false);
        pkg.setCanChoosePt(request.getCanChoosePt() != null ? request.getCanChoosePt() : false);
        pkg.setHasMealPlan(request.getHasMealPlan() != null ? request.getHasMealPlan() : false);
        pkg.setMaxHoldTimes(request.getMaxHoldTimes() != null ? request.getMaxHoldTimes() : 0);
        pkg.setHoldReturnPercent(request.getHoldReturnPercent() != null ? request.getHoldReturnPercent() : 0);

        return toResponse(gymPackageRepository.save(pkg));
    }

    // ----------------------------------------------------------------
    // ADMIN: Xóa gói tập
    // ----------------------------------------------------------------
    @Transactional
    public void deletePackage(Integer id) {
        if (!gymPackageRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy gói tập với id: " + id);
        }
        try {
            gymPackageRepository.deleteById(id);
            gymPackageRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Không thể xóa gói tập này vì đã có hội viên đăng ký. Hãy tạo gói mới thay vì xóa gói cũ!");
        }
    }

    // ----------------------------------------------------------------
    // ADMIN: Thay đổi trạng thái Ẩn/Hiện gói tập
    // ----------------------------------------------------------------
    @Transactional
    public GymPackageResponse togglePackageStatus(Integer id) {
        GymPackage pkg = gymPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy gói tập với id: " + id));
        Boolean currentStatus = pkg.getIsActive() != null ? pkg.getIsActive() : true;
        pkg.setIsActive(!currentStatus);
        return toResponse(gymPackageRepository.save(pkg));
    }

    // ----------------------------------------------------------------
    // HELPER: Entity → Response DTO
    // ----------------------------------------------------------------
    private GymPackageResponse toResponse(GymPackage pkg) {
        return GymPackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .dailyPrice(pkg.getDailyPrice())
                .minDays(pkg.getMinDays())
                .description(pkg.getDescription())
                .hasPt(pkg.getHasPt())
                .canChoosePt(pkg.getCanChoosePt())
                .hasMealPlan(pkg.getHasMealPlan())
                .maxHoldTimes(pkg.getMaxHoldTimes())
                .holdReturnPercent(pkg.getHoldReturnPercent())
                .isActive(pkg.getIsActive() != null ? pkg.getIsActive() : true)
                .build();
    }
}
