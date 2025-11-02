package kr.adapterz.community.common.security.jwt;

public record JWTDto(
        String accessToken, String refreshToken
) {
}
