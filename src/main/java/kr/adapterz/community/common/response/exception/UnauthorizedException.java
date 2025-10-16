package kr.adapterz.community.common.response.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException implements ErrorStatusCodeProvider {
    private final int statusCode = HttpStatus.UNAUTHORIZED.value();

    /**
     * HTTP status code 401을 나타내는 예외입니다.
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public int getHttpStatusValue() {
        return getHttpStatus().value();
    }
}
