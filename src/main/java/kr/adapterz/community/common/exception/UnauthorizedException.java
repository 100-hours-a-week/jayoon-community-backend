package kr.adapterz.community.common.exception;

import kr.adapterz.community.common.message.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException implements ErrorStatusCodeProvider {
    private final int statusCode = HttpStatus.UNAUTHORIZED.value();

    /**
     * HTTP status code 401을 나타내는 예외입니다.
     */
    public UnauthorizedException(ErrorCode message) {
        super(message.getMessage());
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
