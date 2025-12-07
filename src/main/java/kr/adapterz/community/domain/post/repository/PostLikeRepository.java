package kr.adapterz.community.domain.post.repository;

import java.util.Optional;
import kr.adapterz.community.domain.post.entity.PostLike;
import kr.adapterz.community.domain.post.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
