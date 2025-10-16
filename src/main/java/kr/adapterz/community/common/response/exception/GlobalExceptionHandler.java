package kr.adapterz.community.common.response.exception;

import kr.adapterz.community.common.response.dto.ApiResponse;
import kr.adapterz.community.common.response.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller 내에서 발생하는 모든 에러를 핸들링 합니다.
 *
 * Controller 레벨에서 별도로 try-catch문이 없기 때문에 해당 레이어 이후 단계에서 발생한 모든 예외를 여기에서 처리하게 됩니다.
 * Filter 레벨에서 발생하는 에러는 이후에 구현할 예정입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetails errorDetails = ErrorDetails.of(HttpStatus.BAD_REQUEST.value());
        ApiResponse<Void> response = ApiResponse.fail(errorDetails, "잘못된 인자입니다.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
