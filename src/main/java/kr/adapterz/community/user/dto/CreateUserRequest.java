package kr.adapterz.community.user.dto;

public record CreateUserRequest(
        String email,
        String password,
        String nickname,
        String profileImageUrl
) {
}
