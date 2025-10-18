package kr.adapterz.community.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.adapterz.community.post.dto.CreatePostRequest;
import kr.adapterz.community.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Builder
@Getter
@AllArgsConstructor
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false, length = 26)
    private String title;

    @Lob
    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "view_count", nullable = false)
    @ColumnDefault("0")
    private Long viewCount;

    @Column(name = "like_count", nullable = false, columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private Long likeCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private Long commentCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Post() {
    }

    public static Post createFrom(User user, CreatePostRequest request) {
        return Post.builder()
                .user(user)
                .title(request.title())
                .body(request.body())
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .build();
    }
}
