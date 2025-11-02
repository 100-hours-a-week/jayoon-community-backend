package kr.adapterz.community.common.security.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import kr.adapterz.community.common.message.ErrorCode;
import kr.adapterz.community.common.security.jwt.JWTDto;
import kr.adapterz.community.common.security.jwt.JWTManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("jwt")
@RequiredArgsConstructor
public class JWTAuthManager implements AuthManager {
    private final JWTManager jwtManager;

    public static final String ACCESS_COOKIE_NAME = "ACCESS_TOKEN";
    public static final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private final Map<String, Long> refreshTokens = new ConcurrentHashMap<>();


    /**
     * Access token과 Refresh token을 생성하여 헤더 쿠키에 넣습니다.
     * <p>
     * 쿠키에 저장 되어 있는 기존 RT를 삭제합니다.
     * <p>
     * AT, RT를 생성하여, RT는 서버에 저장하고, 두 토큰 모두 쿠키에 첨부합니다.
     *
     * @param userId
     * @param request
     * @param response
     */
    @Override
    public void login(Long userId, HttpServletRequest request, HttpServletResponse response) {
        findCookie(request, REFRESH_COOKIE_NAME)
                .ifPresent(cookie -> refreshTokens.remove(cookie.getValue()));

        JWTDto dto = jwtManager.generateTokens(userId);
        refreshTokens.put(dto.refreshToken(), userId);
        addCookie(response, ACCESS_COOKIE_NAME, dto.accessToken(),
                (int) accessTokenExpirationMs / 1000);
        addCookie(response, REFRESH_COOKIE_NAME, dto.refreshToken(),
                (int) refreshTokenExpirationMs / 1000);
    }

    /**
     * 쿠키에 있는 access token과 refresh token의 값을 비워두고, 만료 기한을 유효하지 않게 변경합니다.
     *
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 서버에서 RT 삭제
        findCookie(request, REFRESH_COOKIE_NAME)
                .ifPresent(cookie -> refreshTokens.remove(cookie.getValue()));
        // 쿠키에서 RT 삭제
        clearCookie(response, REFRESH_COOKIE_NAME);
    }

    /**
     * 쿠키에서 access token이 유효한지 검사한 뒤, userId를 추출하여 반환합니다.
     *
     * @param request
     * @return
     */
    @Override
    public Long getAuthenticatedUserId(HttpServletRequest request) {
        // 요청의 쿠키에서 AT가 유효한지 검사
        Cookie cookie = findCookie(request, REFRESH_COOKIE_NAME)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.TOKEN_INVALID));
        // AT의 페이로드에서 userId 추출하여 반환
        return jwtManager.getUserIdFromToken(cookie.getValue());
    }

    /**
     * 요청에서 쿠키를 조회하여, 쿠키가 없을 때는 Optional.empty()를 반환하고, 존재할 때는 해당 쿠키를 Optional에 감싸서 반환합니다.
     *
     * @param request
     * @param cookieName
     * @return
     */
    private Optional<Cookie> findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
    }

    /**
     * 응답에 쿠키를 첨부합니다.
     * <p>
     * cookie.setSecure(true); // HTTPS 설정을 추가하면 추가합니다.
     *
     * @param response
     * @param cookieName
     * @param value
     * @param maxAgeSeconds
     */
    private void addCookie(HttpServletResponse response, String cookieName, String value,
                           int maxAgeSeconds) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * cookieName을 이름으로 갖는 응답의 쿠키를 만료시킵니다.
     */
    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
