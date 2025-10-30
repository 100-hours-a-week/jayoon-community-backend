package kr.adapterz.community.auth.controller;

import jakarta.validation.Valid;
import kr.adapterz.community.auth.dto.LoginRequestDto;
import kr.adapterz.community.auth.dto.LoginResponseDto;
import kr.adapterz.community.auth.service.AuthService;
import kr.adapterz.community.common.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> createAuth(
            @Valid @RequestBody LoginRequestDto dto) {

        LoginResponseDto loginResponseDto = authService.login(dto);
        ApiResponseDto<LoginResponseDto> responseBody = ApiResponseDto.success(loginResponseDto,
                "로그인에 성공하였습니다.");
        // 인증 정보(세션) 생성 및 쿠키 생성
        // 헤더에 쿠키 첨부
        return ResponseEntity.ok().body(responseBody);
    }
}
