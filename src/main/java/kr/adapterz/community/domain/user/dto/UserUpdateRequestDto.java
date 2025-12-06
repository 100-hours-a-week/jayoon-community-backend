package kr.adapterz.community.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateRequestDto(
        @Size(max = 2048, message = "프로필 이미지 URL은 최대 2048자까지 가능합니다.")
        @URL(message = "프로필 이미지 URL 형식이 올바르지 않습니다.")
        String profileImageUrl,

        @Size(min = 2, max = 10, message = "닉네임은 2~10자로 입력해야 합니다.")
        @Pattern(regexp = "^[\\S]+$", message = "닉네임에 띄어쓰기는 불가능합니다.")
        String nickname,

        @Pattern(
                regexp = UserCreateRequestDto.PASSWORD_REGEX,
                message = "비밀번호는 8~20자이면서 대, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String currentPassword,

        @Pattern(
                regexp = UserCreateRequestDto.PASSWORD_REGEX,
                message = "비밀번호는 8~20자이면서 대, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String updatedPassword
) {
}