package kr.adapterz.community.common.security.jwt;

import lombok.Builder;

/**
 * 7주차에 구현 예정입니다.
 */
@Builder
public record JwtDto(
        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
