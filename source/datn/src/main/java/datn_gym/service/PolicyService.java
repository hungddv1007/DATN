package datn_gym.service;

import datn_gym.dto.response.PolicyVersionResponse;
import datn_gym.entity.*;
import datn_gym.repository.PolicyAcceptanceRepository;
import datn_gym.repository.PolicyVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyVersionRepository policyRepository;
    private final PolicyAcceptanceRepository acceptanceRepository;

    public PolicyVersionResponse getActive(String type) {
        return toResponse(requireActive(type));
    }

    public List<PolicyVersionResponse> getAllActive() {
        return policyRepository.findByIsActiveTrueOrderByPolicyTypeAscVersionNumberDesc()
                .stream().map(this::toResponse).toList();
    }

    public PolicyVersion requireAcceptedVersion(Integer id, String expectedType) {
        PolicyVersion policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên bản điều khoản"));
        if (!Boolean.TRUE.equals(policy.getIsActive()) || !expectedType.equals(policy.getPolicyType())) {
            throw new IllegalArgumentException("Phiên bản điều khoản không còn hiệu lực");
        }
        return policy;
    }

    @Transactional
    public void recordAcceptance(User user, PolicyVersion policy, Transaction transaction,
                                 String context, String ip, String userAgent) {
        acceptanceRepository.save(PolicyAcceptance.builder()
                .user(user).policyVersion(policy).transaction(transaction)
                .acceptanceContext(context).acceptedIp(limit(ip, 64))
                .acceptedUserAgent(limit(userAgent, 500)).build());
    }

    private PolicyVersion requireActive(String type) {
        return policyRepository.findTopByPolicyTypeAndIsActiveTrueOrderByVersionNumberDesc(type)
                .orElseThrow(() -> new IllegalArgumentException("Chưa cấu hình chính sách " + type));
    }

    private PolicyVersionResponse toResponse(PolicyVersion p) {
        return PolicyVersionResponse.builder().id(p.getId()).policyType(p.getPolicyType())
                .versionNumber(p.getVersionNumber()).title(p.getTitle()).content(p.getContent())
                .effectiveAt(p.getEffectiveAt()).build();
    }

    private String limit(String value, int max) {
        return value == null ? null : value.substring(0, Math.min(value.length(), max));
    }
}
