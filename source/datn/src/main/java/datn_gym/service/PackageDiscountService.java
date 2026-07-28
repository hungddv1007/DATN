package datn_gym.service;

import datn_gym.dto.response.PackageDiscountResponse;
import datn_gym.entity.GymPackage;
import datn_gym.entity.PackageDiscount;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.PackageDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageDiscountService {

    private final PackageDiscountRepository discountRepository;
    private final GymPackageRepository packageRepository;

    public List<PackageDiscountResponse> getAll() {
        return discountRepository.findAllByOrderByMinDaysAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PackageDiscountResponse create(Integer packageId, int minDays, int discountPercent) {
        validate(minDays, discountPercent);

        PackageDiscount discount = PackageDiscount.builder()
                .minDays(minDays)
                .discountPercent(discountPercent)
                .build();

        if (packageId != null) {
            GymPackage pkg = packageRepository.findById(packageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
            discount.setGymPackage(pkg);
        }

        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public PackageDiscountResponse update(Integer id, Integer packageId, int minDays, int discountPercent) {
        validate(minDays, discountPercent);

        PackageDiscount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy mốc chiết khấu"));

        discount.setMinDays(minDays);
        discount.setDiscountPercent(discountPercent);

        if (packageId != null) {
            GymPackage pkg = packageRepository.findById(packageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói tập"));
            discount.setGymPackage(pkg);
        } else {
            discount.setGymPackage(null);
        }

        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public void delete(Integer id) {
        if (!discountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy mốc chiết khấu");
        }
        discountRepository.deleteById(id);
    }

    private void validate(int minDays, int discountPercent) {
        if (minDays < 1) throw new IllegalArgumentException("Số ngày tối thiểu phải >= 1");
        if (discountPercent < 1 || discountPercent > 100)
            throw new IllegalArgumentException("% giảm giá phải từ 1 đến 100");
    }

    private PackageDiscountResponse toResponse(PackageDiscount d) {
        return PackageDiscountResponse.builder()
                .id(d.getId())
                .packageId(d.getGymPackage() != null ? d.getGymPackage().getId() : null)
                .packageName(d.getGymPackage() != null ? d.getGymPackage().getName() : "Tất cả gói")
                .minDays(d.getMinDays())
                .discountPercent(d.getDiscountPercent())
                .build();
    }
}
