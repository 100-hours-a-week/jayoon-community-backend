package kr.adapterz.community.domain.post.dto;

import java.util.List;

public record CommentListResponseDto(
    List<CommentResponseDto> comments,
    Long nextCursor,
    Long totalCount
) {
}
