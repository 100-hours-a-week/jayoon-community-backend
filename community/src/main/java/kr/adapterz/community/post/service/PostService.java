package kr.adapterz.community.post.service;

import kr.adapterz.community.post.dto.CreatePostRequest;
import kr.adapterz.community.post.dto.PostResponse;
import kr.adapterz.community.post.entity.Post;
import kr.adapterz.community.post.repository.PostRepository;
import kr.adapterz.community.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponse createPost(CreatePostRequest request) {
        // 더미 데이터
        Long userId = 1L;
        String nickname = "jayoon";
        String profileImageUrl = "profileImageUrl";

        User user = new User(userId, nickname, profileImageUrl);
        Post newPost = Post.createFrom(user, request);

        Post savedPost = postRepository.save(newPost);
        return PostResponse.from(savedPost);
    }
}
