package kr.adapterz.community.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record PostCreateRequestDto(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Size(max = 26, message = "제목은 최대 26자까지 입력 가능합니다.")
        String title,

        @NotBlank(message = "본문은 필수 입력 항목입니다.")
        String body,

        List<@URL(message = "유효하지 않은 URL 형식입니다.") String> imageUrls
) {
}