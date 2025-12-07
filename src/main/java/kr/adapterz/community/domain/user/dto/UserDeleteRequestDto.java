package kr.adapterz.community.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequestDto(
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password
) {
}
