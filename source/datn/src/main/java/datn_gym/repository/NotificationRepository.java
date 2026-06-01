package datn_gym.repository;

import datn_gym.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
    long countByUser_IdAndIsReadFalse(Integer userId);

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