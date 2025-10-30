package kr.adapterz.community.auth.service;

import static kr.adapterz.community.common.message.ErrorCode.AUTH_FAILURE;

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

    /**
     * 이메일, 비밀번호를 통해 인증을 진행하고, 인가를 위한 인증 정보를 생성하여 클라이언트에게 줍니다.
     *
     * @param dto
     * @return
     */
    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        UserAuth userAuth = userService.findUserAuthByEmail(dto.email());
        String passwordHash = userAuth.getPasswordHash();
        if (!encoder.checkPassword(dto.password(), passwordHash)) {
            throw new UnauthorizedException(AUTH_FAILURE);
        }
        User user = userService.findById(userAuth.getUser().getId());
        return LoginResponseDto.of(userAuth.getEmail(), user);
    }

    @Override
    public void logout() {

    }
}
