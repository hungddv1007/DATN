package datn_gym.service;

import datn_gym.dto.request.MemberProfileUpdateRequest;
import datn_gym.dto.response.MemberProfileResponse;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.User;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MembershipRepository membershipRepository;

    // ================================================================
    // MEMBER: Xem hồ sơ thể chất của chính mình
    // ================================================================
    public MemberProfileResponse getMyProfile(String memberEmail) {
        User member = userService.getUserByEmail(memberEmail);

        MemberProfile profile = memberProfileRepository.findByUser_Id(member.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ thể chất"));

        return toResponse(profile);
    }

    // ================================================================
    // PT: Xem hồ sơ thể chất của một hội viên thuộc quyền mình
    // ================================================================
    public MemberProfileResponse getMemberProfile(String ptEmail, Integer memberId) {
        User pt = userService.getUserByEmail(ptEmail);

        // FIX IDOR: Check tại DB — member có thuộc PT này không
        validatePtOwnsMember(pt.getId(), memberId);

        MemberProfile profile = memberProfileRepository.findByUser_Id(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ thể chất"));

        return toResponse(profile);
    }

    // ================================================================
    // PT: Ghi nhận / cập nhật tình trạng thể chất của hội viên
    // UPSERT PATTERN: Nếu chưa có MemberProfile -> tự tạo mới (insert)
    //                 Nếu đã có -> cập nhật (update)
    // Tránh lỗi 404 khi PT đánh giá lần đầu cho hội viên mới
    // ================================================================
    @Transactional
    public MemberProfileResponse updateMemberProfile(String ptEmail, Integer memberId,
                                                       MemberProfileUpdateRequest request) {
        User pt = userService.getUserByEmail(ptEmail);

        // FIX IDOR: Validate TRƯỚC khi load/tạo profile — tránh query thừa nếu fail
        validatePtOwnsMember(pt.getId(), memberId);

        // UPSERT: Tìm profile đã tồn tại, nếu không có thì tạo mới
        MemberProfile profile = memberProfileRepository.findByUser_Id(memberId)
                .orElseGet(() -> {
                    // Chưa có profile -> lấy User để gắn vào profile mới
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Không tìm thấy hội viên"));

                    MemberProfile newProfile = new MemberProfile();
                    newProfile.setUser(member);
                    return newProfile;
                });

        profile.setPhysicalCondition(request.getPhysicalCondition());

        return toResponse(memberProfileRepository.save(profile));
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private void validatePtOwnsMember(Integer ptId, Integer memberId) {
        if (!membershipRepository.existsActiveMembershipByPtAndMember(ptId, memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Hội viên này không thuộc quyền quản lý của bạn");
        }
    }



    // FIX N+1: @EntityGraph đã load user sẵn trong Repository (khi profile đã tồn tại)
    private MemberProfileResponse toResponse(MemberProfile profile) {
        User user = profile.getUser();

        return MemberProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .physicalCondition(profile.getPhysicalCondition())
                .build();
    }
}
