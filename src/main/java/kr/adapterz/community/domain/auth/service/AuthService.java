package kr.adapterz.community.domain.auth.service;

import kr.adapterz.community.domain.auth.dto.LoginRequestDto;
import kr.adapterz.community.domain.auth.dto.LoginResponseDto;

public interface AuthService {
    // 로그인, 인증 자원 생성
    LoginResponseDto login(LoginRequestDto dto);

    // 로그아웃, 인증 자원 제거
    void logout();
}
