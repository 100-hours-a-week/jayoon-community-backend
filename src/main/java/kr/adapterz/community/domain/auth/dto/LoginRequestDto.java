package kr.adapterz.community.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
 */
public record LoginRequestDto(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 320, message = "이메일은 최대 320자까지 입력 가능합니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = PASSWORD_REGEX,
                message = "비밀번호는 8~20자이면서 대, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password
) {
    public static final String PASSWORD_REGEX =
            "^(?=.*[a-z])" +      // 소문자 최소 1개
                    "(?=.*[A-Z])" +      // 대문자 최소 1개
                    "(?=.*\\d)" +         // 숫자 최소 1개
                    "(?=.*[@$!%*?&])" +  // 특수문자 최소 1개
                    "[A-Za-z\\d@$!%*?&]{8,20}$"; // 허용 문자 및 길이
}
