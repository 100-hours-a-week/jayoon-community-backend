package kr.adapterz.community.common.security.auth;

import static kr.adapterz.community.common.message.ErrorCode.TOKEN_INVALID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import kr.adapterz.community.common.security.CookieManager;
import kr.adapterz.community.common.security.jwt.JwtDto;
import kr.adapterz.community.common.security.jwt.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@Profile("jwt")
@RequiredArgsConstructor
public class JwtAuthManager implements AuthManager {
    private final JwtManager jwtManager;
    private final CookieManager cookieManager;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private final Map<String, Long> refreshTokens = new ConcurrentHashMap<>();


    @Override
    public void login(Long userId, HttpServletRequest request, HttpServletResponse response) {
        findCookie(request, CookieManager.REFRESH_TOKEN_COOKIE_NAME)
                .ifPresent(cookie -> refreshTokens.remove(cookie.getValue()));

        JwtDto dto = jwtManager.generateTokens(userId);
        refreshTokens.put(dto.refreshToken(), userId);

        ResponseCookie accessTokenCookie = cookieManager.getAccessTokenCookie(dto.accessToken(), accessTokenExpirationMs / 1000);
        ResponseCookie refreshTokenCookie = cookieManager.getRefreshTokenCookie(dto.refreshToken(), refreshTokenExpirationMs / 1000);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        findCookie(request, CookieManager.REFRESH_TOKEN_COOKIE_NAME)
                .ifPresent(cookie -> refreshTokens.remove(cookie.getValue()));

        response.addHeader("Set-Cookie", cookieManager.clearCookie(CookieManager.ACCESS_TOKEN_COOKIE_NAME, "/").toString());
        response.addHeader("Set-Cookie", cookieManager.clearCookie(CookieManager.REFRESH_TOKEN_COOKIE_NAME, "/auth/refresh").toString());
    }

    @Override
    public Long getAuthenticatedUserId(HttpServletRequest request) {
        Cookie cookie = findCookie(request, CookieManager.ACCESS_TOKEN_COOKIE_NAME)
                .orElseThrow(() -> new UnauthorizedException(TOKEN_INVALID));
        return jwtManager.getAuthenticatedUserIdFromToken(cookie.getValue());
    }

    private Optional<Cookie> findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
    }
}
