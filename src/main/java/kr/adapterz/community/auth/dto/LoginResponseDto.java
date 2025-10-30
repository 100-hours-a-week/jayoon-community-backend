package kr.adapterz.community.auth.dto;

import kr.adapterz.community.user.entity.User;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        Long userId,

        String email,

        String nickname,

        String profileImageUrl
) {
    public static LoginResponseDto of(String email, User user) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(email)
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
