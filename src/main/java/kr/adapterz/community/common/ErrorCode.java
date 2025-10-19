package kr.adapterz.community.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모든 에러 코드를 관리하는 Enum.
 * <p>
 * [네이밍 규칙]
 * <p>
 * 0. 관용적으로 널리 해당 상황에 대한 표현이 있다면 그것을 사용한다.
 * <p>
 * 1. '도메인_상태' 형식으로 작성한다.
 * <p>
 * 2. 도메인은 에러의 주체가 되는 비즈니스 개념(명사)을 의미한다.
 * <p>
 * 3. 상태는 명사 또는 과거분사(p.p.)로 표현하여 에러의 결과를 나타낸다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INPUT_VALUE_INVALID("입력값이 올바르지 않습니다."),
    INTERNAL_ERROR_SERVER("서버에 문제가 발생했습니다. 관리자에게 문의해주세요."),

    // Authentication
    AUTH_FAILURE("이메일 또는 비밀번호가 잘못되었습니다."),
    TOKEN_EXPIRED("인증 토큰이 만료되었습니다."),
    TOKEN_INVALID("인증 토큰이 유효하지 않습니다."),

    // User
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_EMAIL_BLANK("이메일은 필수 입력 값입니다."),
    USER_EMAIL_ALREADY_EXISTED("이미 사용 중인 이메일입니다."),
    USER_NICKNAME_ALREADY_EXISTED("닉네임이 이미 존재합니다."),
    USER_PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),

    // UserAuth
    USER_AUTH_NOT_FOUND("유저 인증 정보가 존재하지 않습니다."),

    // Post
    POST_NOT_FOUND("해당 게시물을 찾을 수 없습니다."),
    POST_ACCESS_DENIED("해당 게시물에 대한 접근 권한이 없습니다.");

    private final String message;
}
