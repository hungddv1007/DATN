package datn_gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    // DRAFT | PUBLISHED
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PUBLISHED"; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
    }
}