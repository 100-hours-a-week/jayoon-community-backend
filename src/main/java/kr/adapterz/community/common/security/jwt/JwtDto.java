package kr.adapterz.community.common.security.jwt;

public record JwtDto(
        String accessToken, String refreshToken
) {
}
