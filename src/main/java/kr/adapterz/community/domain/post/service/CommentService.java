package kr.adapterz.community.domain.post.service;

import kr.adapterz.community.domain.post.dto.CommentListResponseDto;

public interface CommentService {

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
