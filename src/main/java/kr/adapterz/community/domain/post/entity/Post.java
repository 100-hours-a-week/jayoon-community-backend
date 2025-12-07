package kr.adapterz.community.domain.post.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostUpdateRequestDto;
import kr.adapterz.community.domain.user.entity.User;
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

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

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

    /**
     * Post Entity를 생성합니다.
     *
     * 초기 생성이므로 viewCount, likeCount, commentCount는 모두 0이어야 합니다.
     *
     * @param user
     * @param request
     * @return
     */
    public static Post createFrom(User user, PostCreateRequestDto request) {
        return Post.builder()
                .user(user)
                .title(request.title())
                .body(request.body())
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .build();
    }

    /**
     * 게시물의 제목, 본문, 이미지를 변경합니다.
     *
     * 이미지는 orphanRemoval 덕분에 images.clear()를 호출하면 자동으로 기존의 PostImage와의 참조를 끊습니다.
     * 그로써 고아가 된 PostImage는 삭제 대상이 됩니다. DELETE 쿼리를 자동으로 생성합니다.
     *
     * cascade = CascadeType.ALL 때문에 images.addAll() 호출 후 PostImage는 생성 대상이 됩니다.
     * INSERT 쿼리를 자동으로 생성합니다.
     *
     * @param request
     */
    public void update(PostUpdateRequestDto request) {
        if (request.title() != null) {
            this.title = request.title();
        }
        if (request.body() != null) {
            this.body = request.body();
        }
        if (request.imageUrls() != null) {
            this.images.clear();
            List<PostImage> newImages = request.imageUrls().stream()
                    .map(imageUrl -> PostImage.of(this, imageUrl))
                    .toList();
            this.images.addAll(newImages);
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
