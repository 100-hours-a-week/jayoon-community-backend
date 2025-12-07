package kr.adapterz.community.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.community.domain.auth.dto.LoginRequestDto;
import kr.adapterz.community.domain.auth.dto.LoginResponseDto;

public interface AuthService {
    // 로그인, 인증 정보 생성
    LoginResponseDto login(LoginRequestDto dto);

    // 로그아웃, 인증 정보 제거
    void logout(HttpServletRequest request, HttpServletResponse response);
}
