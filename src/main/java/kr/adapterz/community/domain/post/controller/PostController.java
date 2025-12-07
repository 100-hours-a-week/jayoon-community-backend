package kr.adapterz.community.domain.post.controller;

import static kr.adapterz.community.common.message.SuccessCode.POST_CREATE_SUCCESS;

import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.web.annotation.LoginUser;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;
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

    /**
     * 게시글을 생성합니다.
     *
     * @param request
     * @param userId
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(
            @Valid @RequestBody PostCreateRequestDto request,
            @LoginUser Long userId
    ) {
        PostResponseDto newPost = postService.createPost(request, userId);
        ApiResponseDto<PostResponseDto> responseBody = ApiResponseDto.success(newPost,
                POST_CREATE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

}