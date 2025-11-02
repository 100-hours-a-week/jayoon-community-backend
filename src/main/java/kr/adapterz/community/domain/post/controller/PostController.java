package kr.adapterz.community.domain.post.controller;

import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.domain.post.dto.CreatePostRequest;
import kr.adapterz.community.domain.post.dto.PostResponse;
import kr.adapterz.community.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostResponse>> createPost(
            @RequestBody CreatePostRequest request) {
        PostResponse newPost = postService.createPost(request);
        ApiResponseDto<PostResponse> responseBody = ApiResponseDto.success(newPost,
                "게시글 작성을 성공했습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}