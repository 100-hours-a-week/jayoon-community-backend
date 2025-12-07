package kr.adapterz.community.domain.post.dto;

import java.util.List;

public record PostUpdateRequestDto(
    String title,
    String body,
    List<String> imageUrls
) {
}
