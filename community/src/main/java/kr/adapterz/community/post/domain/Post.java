package kr.adapterz.community.post.domain;

import jakarta.persistence.*;
import kr.adapterz.community.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
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
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false, columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false, columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private Long commentCount = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Post() {}

    public Post(User user, String title, String body) {
        this.user = user;
        this.title = title;
        this.body = body;
    }
}
