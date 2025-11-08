package kr.adapterz.community.common.exception.dto;

import kr.adapterz.community.common.message.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException implements ErrorStatusCodeProvider {
    /**
     * HTTP status code 400을 나타내는 범용 예외입니다.
     * <p>
     * 현재 데이터 중복, 인자의 형식 잘못 등 기본적인 클라이언트 에러를 다루고 있습니다. 이후에 세분화 될 예정입니다.
     */
    public BadRequestException(ErrorCode message) {
        super(message.getMessage());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public int getHttpStatusValue() {
        return getHttpStatus().value();
    }
}
