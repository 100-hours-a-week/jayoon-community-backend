package kr.adapterz.community.common.response.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException implements ErrorStatusCodeProvider{

    /**
     * HTTP status code 403을 나타내는 예외입니다.
     */
    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public int getHttpStatusValue() {
        return getHttpStatus().value();
    }
}
