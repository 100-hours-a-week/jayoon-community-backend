package kr.adapterz.community.post.service;

import kr.adapterz.community.post.dto.CreatePostRequest;
import kr.adapterz.community.post.dto.PostResponse;
import kr.adapterz.community.post.entity.Post;
import kr.adapterz.community.post.repository.PostRepository;
import kr.adapterz.community.user.entity.User;
import kr.adapterz.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostResponse createPost(CreatePostRequest request) {
        // ToDo: 인가 구현 후 변경
        Long userId = 1L;
        User user = userService.findById(userId);
        Post newPost = Post.createFrom(user, request);

        Post savedPost = postRepository.save(newPost);
        return PostResponse.from(savedPost);
    }
}
