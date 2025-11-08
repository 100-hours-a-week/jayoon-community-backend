package kr.adapterz.community.domain.post.dto;

import java.util.List;

public record PostCreateRequestDto(
        String title,
        String body,
        List<String> imageUrls
) {
}
