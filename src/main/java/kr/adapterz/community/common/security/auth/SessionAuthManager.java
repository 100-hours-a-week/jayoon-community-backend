package kr.adapterz.community.common.security.auth;

import static kr.adapterz.community.common.message.ErrorCode.INVALID_SESSION;
import static kr.adapterz.community.common.message.ErrorCode.USER_AUTH_NOT_FOUND;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("session")
public class SessionAuthManager implements AuthManager {
    public static final String COOKIE_NAME = "SESSION_ID";
    // 60초 * 60분 * 24시간 * 30일
    public static final int EXPIRES_IN = 60 * 60 * 24 * 30;

    /**
     * session에 해당하는 userId를 저장합니다.
     *
     * 여러 기기나 세션 등의 접근으로 userId는 여러 개가 있을 수 있지만 세션은 독립적이기 때문에 이를 key로 사용합니다.
     * (key, value) = (sessionId, userId)
     */

    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    /**
     * 기존 세션을 제거하고, 새로운 세션을 생성합니다.
     *
     * - 요청 쿠키에 기존 인증 정보가 있다면 제거합니다
     * - 독립적인 세션 아이디가 필요하므로 UUID를 통해 이를 생성합니다.
     *
     * @param userId
     * @param request
     * @param response
     */
    @Override
    public void login(Long userId, HttpServletRequest request,
                      HttpServletResponse response) {
        findCookie(request)
                .ifPresent(cookie -> sessions.remove(cookie.getValue()));
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, userId);
        addAuthInfoInResponse(sessionId, response);
    }

    /**
     * 서버에서 관리 중인 세션과 브라우저에 저장된 인증 정보를 삭제합니다.
     *
     * - 브라우저에 저장 되어 있는 세션도 삭제해야 하므로, 이후에 컨트롤러에서 해당 이름을 갖는 쿠키를 삭제합니다.
     *
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        findCookie(request)
                .ifPresent(cookie -> sessions.remove(cookie.getValue()));
        clearCookie(response, COOKIE_NAME);
    }

    /**
     * 세션이 유효한지 검증하며, 인증된 유저의 userId를 반환합니다.
     *
     * 클라이언트의 요청 쿠키에서 sessionId를 찾고, 서버의 세션 저장소에서 이를 이용해 userId를 찾아 반환합니다.
     * 존재하지 않는다면 401 응답을 합니다.
     * 서버의 세션 저장소에 세션이 존재하지 않는 경우는 현재 서버를 재시동 하여 메모리가 날아가는 경우 밖에 없습니다.
     *
     * @param request
     * @return
     */
    @Override
    public Long getAuthenticatedUserId(HttpServletRequest request) {
        String sessionId = findCookie(request)
                .map(Cookie::getValue)
                .orElseThrow(() -> new UnauthorizedException(USER_AUTH_NOT_FOUND));
        Long userId = sessions.get(sessionId);
        if (userId == null) {
            throw new UnauthorizedException(INVALID_SESSION);
        }
        return userId;
    }

    /**
     * 요청에서 쿠키를 찾습니다.
     *
     * 세션 아이디를 값으로 갖는 쿠키를 찾습니다.
     *
     * @param request
     * @return
     */
    private Optional<Cookie> findCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findFirst();
    }

    /**
     * 응답 쿠키에 세션 아이디를 추가합니다.
     *
     * SameSite는 Lax가 기본값입니다.
     * cookie.setSecure(true); // 현재는 local이므로 HTTPS 사용하지 않음
     *
     * @param authInfo
     * @param response
     */
    private void addAuthInfoInResponse(String authInfo, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, authInfo);
        cookie.setMaxAge(EXPIRES_IN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 응답 쿠키를 만료시킵니다.
     */
    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
