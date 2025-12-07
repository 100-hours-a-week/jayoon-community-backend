package kr.adapterz.community.domain.post.dto;

public record PostLikeCountResponseDto(
    Long postId,
    Long likeCount
) {
}
