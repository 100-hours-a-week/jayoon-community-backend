package kr.adapterz.community.common.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("jwt")
public class JWTAuthManager implements AuthManager {
    /**
     * JWT access token과 refresh token을 생성하여 헤더 쿠키에 넣습니다.
     *
     * @param userId
     * @param request
     * @param response
     */
    @Override
    public void login(Long userId, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 쿠키에 있는 access token과 refresh token의 값을 비워두고, 만료 기한을 유효하지 않게 변경합니다.
     *
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request) {

    }

    /**
     * 쿠키에서 access token이 유효한지 검사한 뒤, JWT의 페이로드에서 userId를 추출하여 요청 객체의 userId 프로퍼티에 삽입합니다. 이후 컨트롤러
     * 등에서 호출하여 사용합니다.
     *
     * @param request
     * @return
     */
    @Override
    public Long getAuthenticatedUserId(HttpServletRequest request) {
        return 0L;
    }
}
