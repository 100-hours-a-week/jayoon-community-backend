package kr.adapterz.community.auth.service;

import static kr.adapterz.community.common.message.ErrorCode.AUTH_FAILURE;

import kr.adapterz.community.auth.dto.JwtDto;
import kr.adapterz.community.auth.dto.LoginRequestDto;
import kr.adapterz.community.auth.dto.LoginResponseDto;
import kr.adapterz.community.common.exception.UnauthorizedException;
import kr.adapterz.community.security.Encoder;
import kr.adapterz.community.user.entity.User;
import kr.adapterz.community.user.entity.UserAuth;
import kr.adapterz.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final Encoder encoder;

    @Override
    public LoginResponseDto createAuth(LoginRequestDto dto) {
        UserAuth userAuth = userService.findUserAuthByEmail(dto.email());
        String passwordHash = userAuth.getPasswordHash();
        if (!encoder.checkPassword(dto.password(), passwordHash)) {
            throw new UnauthorizedException(AUTH_FAILURE);
        }
        User user = userService.findById(userAuth.getUser().getId());
        // 인증 정보 생성, 7주차 구현 예정
        JwtDto jwtDto = new JwtDto("Bearer", "example", "example", 3600L);
        return LoginResponseDto.of(userAuth.getEmail(), user, jwtDto);
    }

    @Override
    public void deleteAuth() {

    }
}
