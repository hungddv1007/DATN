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
        long totalMembers = userRepository.findAll().stream()
                .filter(u -> "MEMBER".equals(u.getRole().getName()) && Boolean.TRUE.equals(u.getStatus()))
                .count();
        long totalPTs = userRepository.findAll().stream()
                .filter(u -> "PT".equals(u.getRole().getName()) && Boolean.TRUE.equals(u.getStatus()))
                .count();
        long totalPackages = gymPackageRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalMembers", totalMembers,
                "totalPTs", totalPTs,
                "totalPlans", totalPackages
        ));
    }
}
