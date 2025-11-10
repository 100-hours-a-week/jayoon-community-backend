package kr.adapterz.community.domain.post.controller;

import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.web.annotation.LoginUser;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;
import kr.adapterz.community.domain.post.service.PostServiceImpl;
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

    private final PostServiceImpl postServiceImpl;

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(
            @RequestBody PostCreateRequestDto request,
            @LoginUser Long userId
    ) {
        PostResponseDto newPost = postServiceImpl.createPost(request, userId);
        ApiResponseDto<PostResponseDto> responseBody = ApiResponseDto.success(newPost,
                "게시글 작성을 성공했습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}