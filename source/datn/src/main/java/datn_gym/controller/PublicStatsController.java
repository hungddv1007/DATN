package datn_gym.controller;

import datn_gym.repository.UserRepository;
import datn_gym.repository.GymPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicStatsController {

    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getPublicStats() {
        // Dùng COUNT query trực tiếp trên DB thay vì load toàn bộ bảng Users
        long totalMembers = userRepository.countByRole_NameAndStatus("MEMBER", true);
        long totalPTs = userRepository.countByRole_NameAndStatus("PT", true);
        long totalPackages = gymPackageRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalMembers", totalMembers,
                "totalPTs", totalPTs,
                "totalPlans", totalPackages
        ));
    }
}
