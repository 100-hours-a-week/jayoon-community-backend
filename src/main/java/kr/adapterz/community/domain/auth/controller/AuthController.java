package kr.adapterz.community.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.security.auth.AuthManager;
import kr.adapterz.community.domain.auth.dto.LoginRequestDto;
import kr.adapterz.community.domain.auth.dto.LoginResponseDto;
import kr.adapterz.community.domain.auth.service.AuthService;
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
    private final AuthManager authManager;

    /**
     * 로그인을 성공하면 인증 정보를 생성하여 클라이언트에게 응답합니다.
     * <p>
     * 인증 정보 및 쿠키를 생성하여 응답에 첨부합니다.
     *
     * @param dto
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> createAuth(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response) {

        LoginResponseDto loginResponseDto = authService.login(dto);
        authManager.login(loginResponseDto.userId(), request, response);

        ApiResponseDto<LoginResponseDto> responseBody = ApiResponseDto.success(loginResponseDto,
                "로그인에 성공하였습니다.");
        return ResponseEntity.ok().body(responseBody);
    }
}
