package kr.adapterz.community.common.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthManager {
    /**
     * HTTP 요청에 있는 기존 인증 정보를 제거하고, 새로운 인증 정보를 생성합니다.
     * <p>
     * 인증 정보에는 유저를 식별할 수 있는 userId가 필요합니다.
     * <p>
     * 인증 정보를 응답 쿠키에 담습니다. 기존에는 컨트롤러에서 이 일을 하려 했으나 세션-쿠키 방식에서 JWT 방식으로 마이그레이션 시 타입과 저장해야 할 데이터의 수가
     * 다르기 때문에 해당 함수에서 진행합니다.
     *
     * @param userId
     * @param request
     * @return 유저의 인증 정보를 식별할 수 있는 값(ex. sessionId, refreshToken's value)
     */
    void login(Long userId, HttpServletRequest request, HttpServletResponse response);

    /**
     * HTTP 요청에 있는 기존 인증 정보를 제거합니다.
     * <p>
     * 인증 정보가 어떠한 형식으로 저장 되어 있는지 알고 있으므로, userId는 필요하지 않습니다.
     *
     * @param request
     */
    void logout(HttpServletRequest request);

    /**
     * 인증 정보가 유효한지 검증하고, 유효하다면 유저 아이디를 반환합니다.
     *
     * @param request
     * @return userId
     */
    Long getAuthenticatedUserId(HttpServletRequest request);
}
