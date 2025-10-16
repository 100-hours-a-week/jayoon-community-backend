package kr.adapterz.community.common.response.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException implements ErrorStatusCodeProvider {
    /**
     * HTTP status code 404을 나타내는 예외입니다.
     */
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public int getHttpStatusValue() {
        return getHttpStatus().value();
    }
}
