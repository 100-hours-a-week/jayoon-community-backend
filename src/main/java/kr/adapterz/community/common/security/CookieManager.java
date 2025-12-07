package kr.adapterz.community.common.security;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieManager {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/auth/refresh";

    private final Environment environment;

    public ResponseCookie getAccessTokenCookie(String token, long maxAge) {
        return createCookie(ACCESS_TOKEN_COOKIE_NAME, token, "/", maxAge);
    }

    public ResponseCookie getRefreshTokenCookie(String token, long maxAge) {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, token, REFRESH_TOKEN_COOKIE_PATH, maxAge);
    }

    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge)
                .httpOnly(true);

        builder.secure(true).sameSite("None");
        return builder.build();
    }

    private boolean isProductionProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    public ResponseCookie clearCookie(String name, String path) {
        return ResponseCookie.from(name, null)
                .path(path)
                .maxAge(0)
                .build();
    }
}
