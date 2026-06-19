package datn_gym.repository;

import datn_gym.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // FIX N+1: Load user + sender trong 1 câu SQL
    @EntityGraph(attributePaths = {"user", "sender"})
    Page<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "sender"})
    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);

    long countByUser_IdAndIsReadFalse(Integer userId);

    // FIX IDOR: Check notification có thuộc user này không — dùng khi xóa
    Optional<Notification> findByIdAndUser_Id(Integer id, Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user.id = :userId")
    int markAsRead(
            @Param("id") Integer id,
            @Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Integer userId);
}
