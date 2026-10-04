package datn_gym.controller;

import datn_gym.dto.request.BlogRequest;
import datn_gym.dto.response.BlogResponse;
import datn_gym.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(
            Authentication authentication,
            @Valid @RequestBody BlogRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(blogService.createBlog(email, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable Integer id,
            @Valid @RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.updateBlog(id, request));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<BlogResponse> toggleBlogStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(blogService.toggleBlogStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Integer id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
}
