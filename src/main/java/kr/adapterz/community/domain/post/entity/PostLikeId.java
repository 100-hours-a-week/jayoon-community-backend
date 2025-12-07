package kr.adapterz.community.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;
}
