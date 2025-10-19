package kr.adapterz.community.common.response.dto;

/**
 * @param <T>은 성공 시 필요하면 응답하게 될 data의 타입입니다.
 */
public record ApiResponseDto<T>(
        boolean success,
        String message,
        T data,
        ErrorDetails error
) {
    /**
     * Controller 내에서 모든 로직 성공 시 응답하게 될 응답 객체입니다.
     *
     * @param <T> 성공하더라도 불필요하면 null이 들어갈 수 있습니다.
     */
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, message, data, null);
    }

    /**
     * Controller 내에서 에러 발생 시 응답하게 될 응답 객체입니다.
     * <T>는 성공 시 필요하면 포함하는 data 필드의 타입입니다. 실패 시 포함하지 않으니 Void로 표시해야 합니다.
     */
    public static <T> ApiResponseDto<T> fail(ErrorDetails error, String message) {
        return new ApiResponseDto<>(false, message, null, error);
    }
}
