package datn_gym.controller;

import datn_gym.dto.response.BlogResponse;
import datn_gym.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class PublicBlogController {

    private final BlogService blogService;

    // Lấy tất cả bài viết PUBLISHED (public, không cần đăng nhập)
    @GetMapping
    public ResponseEntity<List<BlogResponse>> getPublishedBlogs() {
        return ResponseEntity.ok(blogService.getPublishedBlogs());
    }

    // Lấy 1 bài viết theo ID (chỉ PUBLISHED)
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Integer id) {
        return ResponseEntity.ok(blogService.getPublishedBlogById(id));
    }
}
