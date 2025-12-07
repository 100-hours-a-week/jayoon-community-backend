package kr.adapterz.community.domain.post.service;

import kr.adapterz.community.domain.post.dto.CommentCreateRequestDto;
import kr.adapterz.community.domain.post.dto.CommentListResponseDto;
import kr.adapterz.community.domain.post.dto.CommentResponseDto;

public interface CommentService {

    /**
     * 특정 게시물에 댓글을 생성합니다.
     *
     * @param postId
     * @param userId
     * @param request
     * @return
     */
    CommentResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto request);

    /**
     * 특정 게시물의 댓글 목록을 조회합니다.
     *
     * @param postId
     * @param limit
     * @param cursor
     * @param loggedInUserId
     * @return
     */
    CommentListResponseDto findCommentsByPostId(Long postId, Long limit, Long cursor, Long loggedInUserId);
}
