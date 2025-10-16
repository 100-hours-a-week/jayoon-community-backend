package kr.adapterz.community.common.response.exception;

import org.springframework.http.HttpStatus;

/**
 * GlobalExceptionHandler에서 에러를 만들 때마다 매번 HTTP status code를 직접 쳐줘야 하는 까다로움을 없애기 위해 사용 됩니다. 이제 HTTP Status code는 각
 * Exception 클래스 내에서 다룹니다.
 */
public interface ErrorStatusCodeProvider {

    /**
     * Controller의 최종 응답 형태인 ResponseEntity<>의 status에 값을 할당하기 위한 메서드입니다.
     */
    HttpStatus getHttpStatus();

    /**
     * ErrorDetails의 statusCode 정수 값을 할당하기 위한 메서드입니다.
     */
    int getHttpStatusValue();
}
