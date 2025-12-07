package kr.adapterz.community.domain.post.repository;

import kr.adapterz.community.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);
}
