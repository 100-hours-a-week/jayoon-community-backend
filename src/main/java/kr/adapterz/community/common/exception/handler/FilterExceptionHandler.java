package kr.adapterz.community.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.adapterz.community.common.exception.dto.BaseCustomException;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.response.ErrorDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterExceptionHandler implements Filter {
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (BaseCustomException e) {
            // log 필요
            setErrorResponse((HttpServletResponse) response, e);
        } catch (Exception e) {
            // log 필요
            setInternalErrorResponse((HttpServletResponse) response, e);
        }
    }

    /**
     * 커스텀 한 모든 예외를 다룹니다.
     *
     * @param response
     * @param ex
     * @throws IOException
     */
    private void setErrorResponse(HttpServletResponse response, BaseCustomException ex)
            throws IOException {
        response.setStatus(ex.getHttpStatusValue());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorDetails errorDetails = ErrorDetails.of(ex.getHttpStatusValue());
        ApiResponseDto<Void> responseBody = ApiResponseDto.fail(errorDetails, ex.getMessage());
        String jsonResponse = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonResponse);
    }

    /**
     * 핸들링하지 못하는 모든 예외를 500 에러로 응답합니다.
     */
    private void setInternalErrorResponse(HttpServletResponse response, Exception ex)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorDetails responseBody = ErrorDetails.of(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ApiResponseDto<Object> errorResponse = ApiResponseDto.fail(responseBody, ex.getMessage());
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
