package kr.adapterz.community.auth.dto;

import kr.adapterz.community.security.jwt.JwtDto;
import kr.adapterz.community.user.entity.User;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        Long userId,

        String email,

        String nickname,

        String profileImageUrl,

        String tokenType,

        String accessToken,

        Long expiresIn
) {
    public static LoginResponseDto of(String email, User user, JwtDto jwtDto) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(email)
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .tokenType(jwtDto.tokenType())
                .accessToken(jwtDto.accessToken())
                .expiresIn(jwtDto.expiresIn())
                .build();
    }
}
