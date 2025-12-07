package kr.adapterz.community.domain.auth.service;

import static kr.adapterz.community.common.message.ErrorCode.AUTH_FAILURE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import kr.adapterz.community.common.security.auth.AuthManager;
import kr.adapterz.community.common.security.encoding.Encoder;
import kr.adapterz.community.domain.auth.dto.LoginRequestDto;
import kr.adapterz.community.domain.auth.dto.LoginResponseDto;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.entity.UserAuth;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final Encoder encoder;
    private final AuthManager authManager;

    /**
     * 이메일, 비밀번호를 통해 인증을 진행하고, 유저 정보를 생성하여 클라이언트에게 줍니다.
     *
     * 인증 정보를 실제로 생성하고, 응답에 첨부하는 것은 AuthManager에서 진행합니다.
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional
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
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authManager.logout(request, response);
    }
}
