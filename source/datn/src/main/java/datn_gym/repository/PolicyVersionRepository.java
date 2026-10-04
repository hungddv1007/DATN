package datn_gym.repository;

import datn_gym.entity.PolicyVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyVersionRepository extends JpaRepository<PolicyVersion, Integer> {
    Optional<PolicyVersion> findTopByPolicyTypeAndIsActiveTrueOrderByVersionNumberDesc(String policyType);
    List<PolicyVersion> findByIsActiveTrueOrderByPolicyTypeAscVersionNumberDesc();
}
