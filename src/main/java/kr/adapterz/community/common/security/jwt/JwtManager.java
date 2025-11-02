package kr.adapterz.community.common.security.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtManager {
    // ToDo 인증 정보 생성, 7주차 구현 예정
    public JwtDto generateToken(Long userId) {
        return JwtDto.builder()
                .tokenType("Bearer")
                .accessToken("example")
                .refreshToken("example")
                .expiresIn(3600L)
                .build();
    }
}
