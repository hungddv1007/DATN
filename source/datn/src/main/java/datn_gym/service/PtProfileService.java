package datn_gym.service;

import datn_gym.dto.request.UpdatePtProfileRequest;
import datn_gym.dto.response.PtProfileResponse;
import datn_gym.entity.PtProfile;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.ReviewRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PtProfileService {

    private final PtProfileRepository ptProfileRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MembershipRepository membershipRepository;

    // Thay thế Hardcode string bằng hằng số (hoặc bạn có thể dùng Enum)
    private static final String STATUS_ACTIVE = "ACTIVE";

    // ----------------------------------------------------------------
    // PUBLIC: Lấy danh sách tất cả PT (trang công khai)
    // ----------------------------------------------------------------
    public List<PtProfileResponse> getAllPtProfiles() {
        return ptProfileRepository.findAllOrderByRatingScoreDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // PUBLIC: Xem hồ sơ chi tiết một PT theo userId
    // ----------------------------------------------------------------
    public PtProfileResponse getPtProfileByUserId(Integer userId) {
        PtProfile profile = ptProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ PT"));
        return toResponse(profile);
    }

    // ----------------------------------------------------------------
    // PT: Xem hồ sơ của chính mình (dùng email từ JWT)
    // ----------------------------------------------------------------
    public PtProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        PtProfile profile = ptProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ PT"));
        return toResponse(profile);
    }

    // ----------------------------------------------------------------
    // PT: Cập nhật hồ sơ của chính mình
    // ----------------------------------------------------------------
    @Transactional
    public PtProfileResponse updateMyProfile(String email, UpdatePtProfileRequest request) {
        User user = getUserByEmail(email);

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        userRepository.save(user);

        PtProfile profile = ptProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ PT"));

        profile.setSpecialization(request.getSpecialization());
        profile.setBio(request.getBio());
        profile.setCertificates(request.getCertificates());
        ptProfileRepository.save(profile);

        return toResponse(profile);
    }

    // ----------------------------------------------------------------
    // INTERNAL: Cập nhật rating_score sau khi có đánh giá mới
    // ----------------------------------------------------------------
    @Transactional
    public void recalculateRating(Integer ptUserId) {
        PtProfile profile = ptProfileRepository.findByUser_Id(ptUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ PT"));

        Double avg = reviewRepository.calculateAverageRating(ptUserId);

        if (avg != null) {
            BigDecimal rounded = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
            profile.setRatingScore(rounded);
        } else {
            profile.setRatingScore(BigDecimal.ZERO);
        }

        ptProfileRepository.save(profile);
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy User từ email (từ JWT)
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // ----------------------------------------------------------------
    // HELPER: Chuyển Entity → Response DTO
    // ----------------------------------------------------------------
    private PtProfileResponse toResponse(PtProfile profile) {
        User user = profile.getUser();

        // FIX LỖI MEMORY LEAK: Sử dụng countBy thay vì findAll().size()
        int totalMembers = membershipRepository.countByPt_IdAndStatus(user.getId(), STATUS_ACTIVE);
        
        // FIX LỖI MEMORY LEAK: Tương tự, đếm trực tiếp ở Database
        int totalReviews = reviewRepository.countByPt_Id(user.getId());

        return PtProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .specialization(profile.getSpecialization())
                .bio(profile.getBio())
                .certificates(profile.getCertificates())
                .ratingScore(profile.getRatingScore())
                .totalMembers(totalMembers)
                .totalReviews(totalReviews)
                .build();
    }
}