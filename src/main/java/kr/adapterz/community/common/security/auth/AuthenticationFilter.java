package kr.adapterz.community.common.security.auth;

import static kr.adapterz.community.common.message.ErrorCode.TOKEN_INVALID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import kr.adapterz.community.common.config.PermitAllProperties;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    private final AuthManager authManager;
    private final PermitAllProperties permitAllProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 모든 Handler(Rest Controller)에 오는 요청에 대해 인증을 진행합니다.
     *
     * 인증 정보가 필요하지 않는 API지만 지금 리소스 요청을 하는 유저가 누구인지 구분해야 하는 API가 존재합니다.
     * 이를 위해 인증이 필요하지 않은 API도 인증을 진행하고, ACCESS_TOKEN이 존재하지 않더라도 성공적으로 넘어갑니다.
     * 다만, 인증 정보가 필요한 API인데 모든 과정을 거치고, 요청 attribute에 "userId"가 존재하지 않는다면 이는 AT가 없거나 오염된 상황으로 판단합니다.
     *
     * @param request  The request to process, 실제 구현체는 HttpServletRequest입니다.
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the
     *                 request and response to for further processing
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Long userId = authManager.getAuthenticatedUserId(req);
            request.setAttribute("userId", userId);
        } catch (UnauthorizedException e) {
            // userId 추출에 실패해도 일단 넘어갑니다. 아래에서 예외처리를 진행합니다.
        }

        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        String requestPath = req.getRequestURI();

        if (!isPermitted(method, requestPath) && request.getAttribute("userId") == null) {
            throw new UnauthorizedException(TOKEN_INVALID);
        }

        chain.doFilter(request, response);
    }

    /**
     * HTTP method와 리소스를 비교하여 허용된 요청인지 확인합니다.
     *
     * @param method
     * @param requestPath
     * @return 허용된 요청이라면 true, 반대라면 false를 반환합니다.
     */
    private boolean isPermitted(HttpMethod method, String requestPath) {
        Map<HttpMethod, List<String>> paths = permitAllProperties.getPaths();
        if (paths == null) {
            return false;
        }

        List<String> permittedPaths = paths.get(method);
        return permittedPaths != null && permittedPaths.stream()
                .anyMatch(path -> pathMatcher.match(path, requestPath));
    }
}
