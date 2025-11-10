package kr.adapterz.community.common.security.auth;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    private final AuthManager authManager;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    /**
     * key는 HTTP method(POST, GET 등)이고, value는 허용 리소스의 배열입니다.
     *
     * Pattern Matcher를 사용합니다. 예시는 다음과 같습니다.
     * 예시: 만약 모든 GET 요청을 허용하고 싶다면 , HttpMethod.GET, Arrays.asList("/**")
     * 예시: 게시글 목록 및 상세 조회는 허용하고 싶다면 , HttpMethod.GET, Arrays.asList("/posts", "/posts/*")
     */
    private final Map<HttpMethod, List<String>> PERMIT_ALL_PATHS = Map.of(
            HttpMethod.POST, Arrays.asList("/auth", "users")
    );

    /**
     * 인증이 필요하지 않은 API를 제외하고, 모든 Handler(Rest Controller)에 오는 요청에 대해 인증을 진행합니다.
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
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        String requestPath = req.getRequestURI();
        if (isPermitted(method, requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = authManager.getAuthenticatedUserId(req);
        request.setAttribute("userId", userId);

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
        List<String> permittedPaths = PERMIT_ALL_PATHS.get(method);
        if (permittedPaths == null) {
            return false;
        }
        return permittedPaths.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
    }
}
