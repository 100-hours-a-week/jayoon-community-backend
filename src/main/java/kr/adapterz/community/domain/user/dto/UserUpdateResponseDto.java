package kr.adapterz.community.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserUpdateResponseDto(
        String nickname,
        String profileImageUrl
) {
}
