package datn_gym.repository;

import datn_gym.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    List<Blog> findByAuthor_IdOrderByCreatedAtDesc(Integer authorId);
    List<Blog> findByAuthor_IdAndStatusOrderByCreatedAtDesc(Integer authorId, String status);

    @Query("SELECT b FROM Blog b WHERE " +
           "b.status = 'PUBLISHED' AND " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY b.createdAt DESC")
    List<Blog> searchPublishedBlogs(@Param("keyword") String keyword);

    @Query("SELECT b FROM Blog b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY b.createdAt DESC")
    Page<Blog> searchByAdmin(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' ORDER BY b.createdAt DESC")
    List<Blog> findTop5Latest(Pageable pageable);
}