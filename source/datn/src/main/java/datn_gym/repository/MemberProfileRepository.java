package datn_gym.repository;

import datn_gym.entity.MemberProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {

    // FIX N+1: Load user trong 1 câu SQL (pattern giống PtProfileRepository)
    @EntityGraph(attributePaths = {"user"})
    Optional<MemberProfile> findByUser_Id(Integer userId);

    boolean existsByUser_Id(Integer userId);
}
