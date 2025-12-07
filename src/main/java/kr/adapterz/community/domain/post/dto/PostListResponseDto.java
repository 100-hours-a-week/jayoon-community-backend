package kr.adapterz.community.domain.post.dto;

import java.util.List;

public record PostListResponseDto(
        List<PostSummaryResponseDto> posts,
        Long nextCursor,
        Long totalCount
) {
}
