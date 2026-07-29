package datn_gym.repository;

import datn_gym.entity.Diet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Integer> {

    // === PT Side ===

    // Tìm mẫu TRAINING_DAY hoặc REST_DAY của 1 cặp PT-Member
    @EntityGraph(attributePaths = {"pt", "member"})
    Optional<Diet> findByPt_IdAndMember_IdAndDayType(Integer ptId, Integer memberId, String dayType);

    // Tìm tất cả diet (mẫu + specific) của 1 cặp PT-Member
    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByPt_IdAndMember_IdOrderByCreatedAtDesc(Integer ptId, Integer memberId);

    // === Member Side ===

    // Tìm mẫu TRAINING_DAY hoặc REST_DAY cho member (không phân biệt PT)
    @EntityGraph(attributePaths = {"pt", "member"})
    Optional<Diet> findByMember_IdAndDayType(Integer memberId, String dayType);

    // Tìm diet ngày cụ thể (SPECIFIC_DATE)
    @EntityGraph(attributePaths = {"pt", "member"})
    Optional<Diet> findByMember_IdAndDayTypeAndDietDate(Integer memberId, String dayType, LocalDate dietDate);

    // Tìm tất cả diet của member
    @EntityGraph(attributePaths = {"pt", "member"})
    List<Diet> findByMember_IdOrderByCreatedAtDesc(Integer memberId);

    // Kiểm tra đã có mẫu hay chưa (tránh trùng)
    boolean existsByPt_IdAndMember_IdAndDayType(Integer ptId, Integer memberId, String dayType);

    // Kiểm tra đã có diet ngày cụ thể chưa
    boolean existsByMember_IdAndDayTypeAndDietDate(Integer memberId, String dayType, LocalDate dietDate);
}
