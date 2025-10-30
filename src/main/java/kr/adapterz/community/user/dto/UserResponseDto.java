package kr.adapterz.community.user.dto;

import kr.adapterz.community.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long userId,

        String email,

        String nickname,

        String profileImageUrl
) {
    public static UserResponseDto of(User user, String email) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .email(email)
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
