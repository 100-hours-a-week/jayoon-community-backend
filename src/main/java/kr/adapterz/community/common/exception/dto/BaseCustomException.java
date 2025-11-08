package kr.adapterz.community.common.exception.dto;

import org.springframework.http.HttpStatus;

public class BaseCustomException extends RuntimeException implements ErrorStatusCodeProvider {
    public BaseCustomException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public int getHttpStatusValue() {
        return 0;
    }
}
