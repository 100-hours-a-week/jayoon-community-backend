package kr.adapterz.community.post.repository;

import kr.adapterz.community.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
