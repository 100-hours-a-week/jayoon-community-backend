package kr.adapterz.community.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.adapterz.community.domain.user.entity.User;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Table(name = "post_like")
public class PostLike {
    @EmbeddedId
    private PostLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

        @CreationTimestamp

        @Column(name = "created_at", nullable = false, updatable = false)

        private LocalDateTime createdAt;

    

        protected PostLike() {

        }

    

        public PostLike(User user, Post post) {

            this.id = new PostLikeId(user.getId(), post.getId());

            this.user = user;

            this.post = post;

        }

    }

    