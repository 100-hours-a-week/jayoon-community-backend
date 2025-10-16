package kr.adapterz.community.common.response.exception;

import kr.adapterz.community.common.response.dto.ApiResponse;
import kr.adapterz.community.common.response.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller 내에서 발생하는 모든 에러를 핸들링 합니다. Controller 레벨에서 별도로 try-catch문이 없기 때문에 해당 레이어 이후 단계에서 발생한 모든 예외를 여기에서 처리하게 됩니다.
 * <p>
 * Notes Controller 레이어 보다 이전에서 예외가 발생하면? 예) 인증, 인가 등 아직 제대로 다뤄보진 않았지만 Filter 레벨에서 발생하는 에러는 이후에 구현할 예정입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HTTP status code 400 예외를 일괄적으로 처리합니다.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex) {
        ErrorDetails errorDetails = ErrorDetails.of(ex.getHttpStatusValue());
        ApiResponse<Void> response = ApiResponse.fail(errorDetails, ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    /**
     * HTTP status code 404 예외를 일괄적으로 처리합니다.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        ErrorDetails errorDetails = ErrorDetails.of(ex.getHttpStatusValue());
        ApiResponse<Void> response = ApiResponse.fail(errorDetails, ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    /**
     * 서버에서 다루지 못한 모든 예외를 statusCode 500번으로 처리합니다.
     * <p>
     * 위의 핸들러 메서드에서 catch 되지 않은 모든 예외들이 여기에서 처리됩니다. 서버에서 개발자가 직접 다루지 못한 예외이므로 해당 예외 객체 ex는 ErrorStatusCodeProvider를 구현하지
     * 않았습니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        ErrorDetails errorDetails = ErrorDetails.of(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ApiResponse<Void> response = ApiResponse.fail(errorDetails, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
