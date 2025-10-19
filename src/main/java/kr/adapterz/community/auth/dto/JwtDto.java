package kr.adapterz.community.auth.dto;

/**
 * 7주차에 구현 예정입니다.
 */
public record JwtDto(
        String tokenType,
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
