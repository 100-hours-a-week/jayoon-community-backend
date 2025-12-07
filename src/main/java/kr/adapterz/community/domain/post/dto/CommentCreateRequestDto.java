package kr.adapterz.community.domain.post.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(
    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    String body
) {
}
