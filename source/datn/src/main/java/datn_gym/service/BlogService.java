package datn_gym.service;

import datn_gym.dto.request.BlogRequest;
import datn_gym.dto.response.BlogResponse;
import datn_gym.entity.Blog;
import datn_gym.entity.User;
import datn_gym.repository.BlogRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // Lấy tất cả bài viết (Trừ DELETED)
    public List<BlogResponse> getAllBlogs() {
        return blogRepository.findAll().stream()
                .filter(b -> !"DELETED".equals(b.getStatus()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Tạo bài viết mới
    @Transactional
    public BlogResponse createBlog(String email, BlogRequest request) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setThumbnail(request.getThumbnail());
        blog.setStatus(request.getStatus() != null ? request.getStatus() : "PUBLISHED");
        blog.setAuthor(author);

        return toResponse(blogRepository.save(blog));
    }

    // Cập nhật bài viết
    @Transactional
    public BlogResponse updateBlog(Integer id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setThumbnail(request.getThumbnail());
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
        }

        return toResponse(blogRepository.save(blog));
    }

    // Đổi trạng thái DRAFT / PUBLISHED
    @Transactional
    public BlogResponse toggleBlogStatus(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        if ("PUBLISHED".equals(blog.getStatus())) {
            blog.setStatus("DRAFT");
        } else {
            blog.setStatus("PUBLISHED");
        }

        return toResponse(blogRepository.save(blog));
    }

    // Xóa bài viết (Soft Delete)
    @Transactional
    public void deleteBlog(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));
        blog.setStatus("DELETED");
        blogRepository.save(blog);
    }

    private BlogResponse toResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .thumbnail(blog.getThumbnail())
                .status(blog.getStatus())
                .authorName(blog.getAuthor().getFullName())
                .createdAt(blog.getCreatedAt())
                .build();
    }
}
