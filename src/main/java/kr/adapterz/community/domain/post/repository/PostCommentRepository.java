package kr.adapterz.community.domain.post.repository;

import java.util.List;
import kr.adapterz.community.domain.post.entity.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    long countByPostId(Long postId);

    List<PostComment> findByPostIdOrderByIdDesc(Long postId, Pageable pageable);

    List<PostComment> findByPostIdAndIdLessThanOrderByIdDesc(Long postId, Long id, Pageable pageable);

}
