package kr.adapterz.community.domain.image.dto;

public record PreSignedUrlResponse(
        String preSignedUrl,
        String imageUrl
) {
}
