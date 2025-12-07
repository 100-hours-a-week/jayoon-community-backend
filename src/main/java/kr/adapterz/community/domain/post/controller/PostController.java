package kr.adapterz.community.domain.post.controller;

import static kr.adapterz.community.common.message.SuccessCode.COMMENT_CREATE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.COMMENT_UPDATE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.POST_CREATE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.POST_DELETE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.POST_GET_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.POST_UPDATE_SUCCESS;

import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.web.annotation.LoginUser;
import kr.adapterz.community.domain.post.dto.CommentCreateRequestDto;
import kr.adapterz.community.domain.post.dto.CommentListResponseDto;
import kr.adapterz.community.domain.post.dto.CommentResponseDto;
import kr.adapterz.community.domain.post.dto.CommentUpdateRequestDto;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostListResponseDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;
import kr.adapterz.community.domain.post.dto.PostUpdateRequestDto;
import kr.adapterz.community.domain.post.service.CommentService;
import kr.adapterz.community.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

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

    /**
     * 게시글을 상세 조회합니다.
     *
     * @param postId
     * @param userId
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getPost(
            @PathVariable Long postId,
            @LoginUser Long userId
    ) {
        PostResponseDto post = postService.findPostDetailById(postId, userId);
        ApiResponseDto<PostResponseDto> responseBody = ApiResponseDto.success(post,
                POST_GET_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param limit
     * @param cursor
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<PostListResponseDto>> getPosts(
            @RequestParam(defaultValue = "10") Long limit,
            @RequestParam(required = false) Long cursor
    ) {
        PostListResponseDto posts = postService.findPostSummaries(limit, cursor);
        ApiResponseDto<PostListResponseDto> responseBody = ApiResponseDto.success(posts,
                POST_GET_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param postId
     * @param userId
     * @param request
     * @return
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> editPost(
            @PathVariable Long postId,
            @LoginUser Long userId,
            @RequestBody PostUpdateRequestDto request
    ) {
        PostResponseDto updatedPost = postService.editPost(userId, postId, request);
        ApiResponseDto<PostResponseDto> responseBody = ApiResponseDto.success(updatedPost,
                POST_UPDATE_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId
     * @param userId
     * @return
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponseDto<Void>> deletePost(
            @PathVariable Long postId,
            @LoginUser Long userId
    ) {
        postService.deletePost(userId, postId);
        ApiResponseDto<Void> responseBody = ApiResponseDto.success(null,
                POST_DELETE_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 특정 게시물의 댓글 목록을 조회합니다.
     *
     * @param postId
     * @param limit
     * @param cursor
     * @param userId
     * @return
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponseDto<CommentListResponseDto>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "10") Long limit,
            @RequestParam(required = false) Long cursor,
            @LoginUser Long userId
    ) {
        CommentListResponseDto comments = commentService.findCommentsByPostId(postId, limit, cursor,
                userId);
        ApiResponseDto<CommentListResponseDto> responseBody = ApiResponseDto.success(comments,
                POST_GET_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 특정 게시물에 댓글을 생성합니다.
     *
     * @param postId
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> createComment(
            @PathVariable Long postId,
            @LoginUser Long userId,
            @Valid @RequestBody CommentCreateRequestDto request
    ) {
        CommentResponseDto newComment = commentService.createComment(postId, userId, request);
        ApiResponseDto<CommentResponseDto> responseBody = ApiResponseDto.success(newComment,
                COMMENT_CREATE_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 특정 댓글을 수정합니다.
     *
     * @param postId
     * @param commentId
     * @param userId
     * @param request
     * @return
     */
    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @LoginUser Long userId,
            @RequestBody CommentUpdateRequestDto request
    ) {
        CommentResponseDto updatedComment = commentService.updateComment(postId, commentId, userId,
                request);
        ApiResponseDto<CommentResponseDto> responseBody = ApiResponseDto.success(updatedComment,
                COMMENT_UPDATE_SUCCESS.getMessage());
        return ResponseEntity.ok(responseBody);
    }
}
    