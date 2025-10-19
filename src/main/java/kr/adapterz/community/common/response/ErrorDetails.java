package kr.adapterz.community.common.response;

/**
 * Frontend 쪽에서 개발 시 편의를 위해 만든 statusCode 필드입니다. HTTP status code와 완전히 동일합니다. 만든 이유는 HTTP status code는 WS나 브라우저 등 애플리케이션
 * 아래 단계에서 보는 정보라고 생각했습니다. 최대한 애플리케이션 레벨만 집중하기 위해 정의했습니다.
 */
public record ErrorDetails(
        int statusCode
) {
    public static ErrorDetails of(int statusCode) {
        return new ErrorDetails(statusCode);
    }
}
