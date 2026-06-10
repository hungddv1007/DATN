package datn_gym.service;

import datn_gym.dto.request.ReviewRequest;
import datn_gym.dto.request.ReviewUpdateRequest;
import datn_gym.dto.response.ReviewResponse;
import datn_gym.entity.Review;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.ReviewRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PtProfileService ptProfileService; // Gọi recalculateRating sau mỗi thay đổi

    private static final String STATUS_ACTIVE = "ACTIVE";

    // ----------------------------------------------------------------
    // PUBLIC: Xem tất cả đánh giá của một PT (trang hồ sơ PT công khai)
    // ----------------------------------------------------------------
    public List<ReviewResponse> getReviewsByPt(Integer ptId) {
        // Kiểm tra PT có tồn tại không
        if (!userRepository.existsById(ptId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy PT");
        }
        return reviewRepository.findByPt_IdOrderByCreatedAtDesc(ptId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // MEMBER: Xem đánh giá mình đã gửi
    // ----------------------------------------------------------------
    public List<ReviewResponse> getMyReviews(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        return reviewRepository.findByMember_IdOrderByCreatedAtDesc(member.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // MEMBER: Tạo đánh giá mới
    // ----------------------------------------------------------------
    @Transactional
    public ReviewResponse createReview(String memberEmail, ReviewRequest request) {
        User member = getUserByEmail(memberEmail);
        User pt = getPtById(request.getPtId());

        // Kiểm tra HV có membership ACTIVE với PT này không
        // Chỉ Premium/VIP mới có PT → đảm bảo đúng nghiệp vụ
        validateMemberHasPt(member.getId(), pt.getId());

        // FIX: Mỗi HV chỉ đánh giá 1 lần cho mỗi PT
        if (reviewRepository.existsByMember_IdAndPt_Id(member.getId(), pt.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bạn đã đánh giá PT này rồi. Vui lòng chỉnh sửa đánh giá cũ.");
        }

        Review review = Review.builder()
                .member(member)
                .pt(pt)
                .ratingStar(request.getRatingStar())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Cập nhật rating_score trong pt_profiles sau khi có đánh giá mới
        ptProfileService.recalculateRating(pt.getId());

        return toResponse(saved);
    }

    // ----------------------------------------------------------------
    // MEMBER: Sửa đánh giá (chỉ sửa ratingStar và comment)
    // ----------------------------------------------------------------
    @Transactional
    public ReviewResponse updateReview(String memberEmail, Integer reviewId,
                                       ReviewUpdateRequest request) {
        User member = getUserByEmail(memberEmail);

        // FIX Lỗi 2: Phân biệt 404 vs 403
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá"));

        // Chỉ HV tạo ra mới được sửa
        if (!review.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền sửa đánh giá này");
        }

        review.setRatingStar(request.getRatingStar());
        review.setComment(request.getComment());
        Review saved = reviewRepository.save(review);

        // Cập nhật lại rating_score sau khi sửa
        ptProfileService.recalculateRating(review.getPt().getId());

        return toResponse(saved);
    }

    // ----------------------------------------------------------------
    // MEMBER: Xóa đánh giá
    // ----------------------------------------------------------------
    @Transactional
    public void deleteReview(String memberEmail, Integer reviewId) {
        User member = getUserByEmail(memberEmail);

        // FIX Lỗi 2: Phân biệt 404 vs 403
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá"));

        // Chỉ HV tạo ra mới được xóa
        if (!review.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền xóa đánh giá này");
        }

        Integer ptId = review.getPt().getId();
        reviewRepository.delete(review);

        // Cập nhật lại rating_score sau khi xóa
        ptProfileService.recalculateRating(ptId);
    }

    // ----------------------------------------------------------------
    // HELPER: Kiểm tra HV có membership ACTIVE với PT này không
    // Đảm bảo chỉ Premium/VIP (có PT) mới được đánh giá
    // ----------------------------------------------------------------
    private void validateMemberHasPt(Integer memberId, Integer ptId) {
        boolean hasActiveMembership = membershipRepository
                .existsActiveMembershipByPtAndMember(ptId, memberId);

        if (!hasActiveMembership) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn chưa có gói tập với PT này hoặc gói tập đã hết hạn");
        }
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy User theo email
    // ----------------------------------------------------------------
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // ----------------------------------------------------------------
    // HELPER: Lấy PT theo ID, kiểm tra đúng role PT
    // ----------------------------------------------------------------
    private User getPtById(Integer ptId) {
        User pt = userRepository.findById(ptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy PT"));

        // FIX IDOR: Đảm bảo user được đánh giá là PT, không phải Admin hay Member
        if (!"PT".equals(pt.getRole().getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Người dùng này không phải PT");
        }

        return pt;
    }

    // ----------------------------------------------------------------
    // HELPER: Chuyển Entity → Response DTO
    // FIX N+1: @EntityGraph đã load sẵn pt và member trong Repository
    // ----------------------------------------------------------------
    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .memberId(review.getMember().getId())
                .memberName(review.getMember().getFullName())
                .memberAvatar(review.getMember().getAvatar())
                .ptId(review.getPt().getId())
                .ptName(review.getPt().getFullName())
                .ratingStar(review.getRatingStar())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
