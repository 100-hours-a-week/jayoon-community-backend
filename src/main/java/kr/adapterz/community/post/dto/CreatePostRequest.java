package kr.adapterz.community.post.dto;

import java.util.List;

public record CreatePostRequest(
        String title,
        String body,
        List<String> imageUrls
) {
}
