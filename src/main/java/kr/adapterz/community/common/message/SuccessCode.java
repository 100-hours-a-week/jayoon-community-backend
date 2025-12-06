package kr.adapterz.community.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모든 성공 코드를 관리하는 Enum.
 *
 * [네이밍 규칙]
 * 1. 도메인_행위 형식으로 작성한다.
 * 2. 도메인은 API의 주체가 되는 비즈니스 개념(명사)을 의미한다.
 * 3. 행위는 GET, CREATE, UPDATE, DELETE 등 CRUD를 의미한다.
 */
@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Common
    OK("요청이 성공적으로 처리되었습니다."),

    // User
    USER_CREATE_SUCCESS("회원가입을 성공했습니다."),
    USER_GET_SUCCESS("사용자 조회가 성공적으로 완료되었습니다."),
    USER_UPDATE_SUCCESS("회원정보 수정이 성공적으로 완료되었습니다."),

    // Image
    PRE_SIGNED_URL_ISSUED("Pre-signed URL이 성공적으로 발급되었습니다."),
    IMAGE_UPLOAD_SUCCESS("이미지 업로드에 성공했습니다."),

    // Post
    POST_CREATE_SUCCESS("게시물 생성이 성공적으로 완료되었습니다."),
    POST_GET_SUCCESS("게시물 조회가 성공적으로 완료되었습니다."),
    POST_UPDATE_SUCCESS("게시물 수정이 성공적으로 완료되었습니다."),
    POST_DELETE_SUCCESS("게시물 삭제가 성공적으로 완료되었습니다.");


    private final String message;
}
