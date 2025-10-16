package kr.adapterz.community.post;

import kr.adapterz.community.common.response.dto.ApiResponse;
import kr.adapterz.community.post.dto.CreatePostRequest;
import kr.adapterz.community.post.dto.PostResponse;
import kr.adapterz.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody CreatePostRequest request) {
        PostResponse newPost = postService.createPost(request);
        ApiResponse<PostResponse> responseBody = ApiResponse.success(newPost, "게시글 작성을 성공했습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}