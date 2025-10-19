package kr.adapterz.community.auth;

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
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto dto) {

        LoginResponseDto loginResponseDto = authService.createAuth(dto);
        ApiResponseDto<LoginResponseDto> responseBody = ApiResponseDto.success(loginResponseDto, "로그인에 성공하였습니다.");
        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE)
                .body(responseBody);
    }
}
