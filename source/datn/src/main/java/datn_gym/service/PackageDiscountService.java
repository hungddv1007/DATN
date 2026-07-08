package datn_gym.service;

import datn_gym.dto.request.PackageDiscountRequest;
import datn_gym.dto.response.PackageDiscountResponse;
import datn_gym.entity.GymPackage;
import datn_gym.entity.PackageDiscount;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.PackageDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageDiscountService {

    private final PackageDiscountRepository discountRepository;
    private final GymPackageRepository packageRepository;

    public List<PackageDiscountResponse> getAllDiscounts() {
        return discountRepository.findAllByOrderByMinDaysAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PackageDiscountResponse createDiscount(PackageDiscountRequest request) {
        GymPackage pkg = null;
        if (request.getPackageId() != null) {
            pkg = packageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập"));
        }

        PackageDiscount discount = PackageDiscount.builder()
                .gymPackage(pkg)
                .minDays(request.getMinDays())
                .discountPercent(request.getDiscountPercent())
                .build();
        
        discountRepository.save(discount);
        return toResponse(discount);
    }

    @Transactional
    public PackageDiscountResponse updateDiscount(Integer id, PackageDiscountRequest request) {
        PackageDiscount discount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chiết khấu"));

        GymPackage pkg = null;
        if (request.getPackageId() != null) {
            pkg = packageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói tập"));
        }

        discount.setGymPackage(pkg);
        discount.setMinDays(request.getMinDays());
        discount.setDiscountPercent(request.getDiscountPercent());
        
        discountRepository.save(discount);
        return toResponse(discount);
    }

    @Transactional
    public void deleteDiscount(Integer id) {
        if (!discountRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy chiết khấu");
        }
        discountRepository.deleteById(id);
    }

    private PackageDiscountResponse toResponse(PackageDiscount d) {
        return PackageDiscountResponse.builder()
                .id(d.getId())
                .packageId(d.getGymPackage() != null ? d.getGymPackage().getId() : null)
                .packageName(d.getGymPackage() != null ? d.getGymPackage().getName() : "Tất cả")
                .minDays(d.getMinDays())
                .discountPercent(d.getDiscountPercent())
                .build();
    }
}
