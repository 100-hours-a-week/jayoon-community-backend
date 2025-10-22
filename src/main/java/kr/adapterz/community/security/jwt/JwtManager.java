package kr.adapterz.community.security.jwt;

import org.springframework.stereotype.Service;

@Service
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
