package datn_gym.service;

import datn_gym.dto.request.MemberProfileUpdateRequest;
import datn_gym.dto.response.MemberProfileResponse;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.User;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.UserRepository;
import datn_gym.util.BodyFatEstimator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

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
                .orElse(null);
        return toResponse(member, profile);
    }

    // ================================================================
    // PT: Xem hồ sơ thể chất của một hội viên thuộc quyền mình
    // ================================================================
    public MemberProfileResponse getMemberProfile(String ptEmail, Integer memberId) {
        User pt = userService.getUserByEmail(ptEmail);

        // FIX IDOR: Check tại DB — member có thuộc PT này không
        validatePtOwnsMember(pt.getId(), memberId);

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hội viên"));
        MemberProfile profile = memberProfileRepository.findByUser_Id(memberId)
                .orElse(null);
        return toResponse(member, profile);
    }

    // ================================================================
    // MEMBER: Tự cập nhật hồ sơ thể chất của chính mình
    // ================================================================
    @Transactional
    public MemberProfileResponse updateMyProfile(
            String memberEmail,
            MemberProfileUpdateRequest request) {
        User member = userService.getUserByEmail(memberEmail);
        MemberProfile profile = memberProfileRepository.findByUser_Id(member.getId())
                .orElseGet(() -> {
                    MemberProfile newProfile = new MemberProfile();
                    newProfile.setUser(member);
                    return newProfile;
                });

        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setBiologicalSex(request.getBiologicalSex());
        profile.setChestCm(request.getChestCm());
        profile.setWaistCm(request.getWaistCm());
        profile.setHipCm(request.getHipCm());
        BodyFatEstimator.estimateAdult(
                        request.getHeightCm(),
                        request.getWeightKg(),
                        request.getDateOfBirth(),
                        request.getBiologicalSex(),
                        LocalDate.now())
                .ifPresentOrElse(
                        estimatedBodyFat -> {
                            profile.setBodyFatPercentage(estimatedBodyFat);
                            profile.setBodyFatSource("ESTIMATED");
                        },
                        () -> {
                            profile.setBodyFatPercentage(request.getBodyFatPercentage());
                            profile.setBodyFatSource(request.getBodyFatPercentage() != null
                                    ? "MANUAL"
                                    : null);
                        });
        profile.setActivityLevel(request.getActivityLevel());
        profile.setFitnessGoal(request.getFitnessGoal());
        profile.setTargetWeightKg(request.getTargetWeightKg());
        profile.setTrainingExperience(trimToNull(request.getTrainingExperience()));
        profile.setInjuryHistory(trimToNull(request.getInjuryHistory()));
        profile.setMedicalConditions(trimToNull(request.getMedicalConditions()));

        return toResponse(member, memberProfileRepository.save(profile));
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



    private MemberProfileResponse toResponse(User user, MemberProfile profile) {
        return MemberProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .heightCm(profile != null ? profile.getHeightCm() : null)
                .weightKg(profile != null ? profile.getWeightKg() : null)
                .dateOfBirth(profile != null ? profile.getDateOfBirth() : null)
                .biologicalSex(profile != null ? profile.getBiologicalSex() : null)
                .chestCm(profile != null ? profile.getChestCm() : null)
                .waistCm(profile != null ? profile.getWaistCm() : null)
                .hipCm(profile != null ? profile.getHipCm() : null)
                .bodyFatPercentage(profile != null ? profile.getBodyFatPercentage() : null)
                .bodyFatSource(profile != null ? profile.getBodyFatSource() : null)
                .activityLevel(profile != null ? profile.getActivityLevel() : null)
                .fitnessGoal(profile != null ? profile.getFitnessGoal() : null)
                .targetWeightKg(profile != null ? profile.getTargetWeightKg() : null)
                .trainingExperience(profile != null ? profile.getTrainingExperience() : null)
                .injuryHistory(profile != null ? profile.getInjuryHistory() : null)
                .medicalConditions(profile != null ? profile.getMedicalConditions() : null)
                .build();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
