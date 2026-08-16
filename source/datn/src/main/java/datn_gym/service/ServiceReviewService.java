package datn_gym.service;

import datn_gym.dto.request.ServiceReviewRequest;
import datn_gym.dto.request.ServiceReviewFeaturedRequest;
import datn_gym.dto.response.ServiceReviewResponse;
import datn_gym.entity.*;
import datn_gym.repository.ServiceReviewRepository;
import datn_gym.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceReviewService {
    private final ServiceReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public List<ServiceReviewResponse> getFeatured() {
        return reviewRepository.findByIsFeaturedTrueOrderByUpdatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<ServiceReviewResponse> getMine(String email) {
        User member = userService.getUserByEmail(email);
        return reviewRepository.findByMember_IdOrderByCreatedAtDesc(member.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<ServiceReviewResponse> getAllForAdmin() {
        return reviewRepository.findAllFeaturedFirst().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ServiceReviewResponse create(String email, ServiceReviewRequest request) {
        User member = userService.getUserByEmail(email);
        Transaction tx = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch"));
        if (!tx.getMembership().getUser().getId().equals(member.getId()) || !"CONFIRMED".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá giao dịch đã thanh toán của chính bạn");
        }
        if (reviewRepository.existsByTransaction_Id(tx.getId())) {
            throw new IllegalArgumentException("Giao dịch này đã được đánh giá");
        }
        ServiceReview review = ServiceReview.builder().member(member).transaction(tx)
                .ratingStar(request.getRatingStar()).comment(request.getComment().trim())
                .displayName(request.isDisplayName()).build();
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ServiceReviewResponse setFeatured(Integer id, ServiceReviewFeaturedRequest request) {
        ServiceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
        review.setIsFeatured(request.isFeatured());
        return toResponse(reviewRepository.save(review));
    }

    private ServiceReviewResponse toResponse(ServiceReview r) {
        boolean show = Boolean.TRUE.equals(r.getDisplayName());
        return ServiceReviewResponse.builder().id(r.getId()).memberId(r.getMember().getId())
                .memberName(show ? r.getMember().getFullName() : "Hội viên GymPro")
                .memberAvatar(show ? r.getMember().getAvatar() : null)
                .packageName(r.getTransaction().getMembership().getGymPackage().getName())
                .transactionId(r.getTransaction().getId()).ratingStar(r.getRatingStar())
                .comment(r.getComment()).displayName(show)
                .featured(Boolean.TRUE.equals(r.getIsFeatured())).createdAt(r.getCreatedAt()).build();
    }
}
