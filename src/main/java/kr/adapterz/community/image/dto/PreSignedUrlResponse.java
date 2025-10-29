package kr.adapterz.community.image.dto;

public record PreSignedUrlResponse(
        String preSignedUrl,
        String imageUrl
) {
}
