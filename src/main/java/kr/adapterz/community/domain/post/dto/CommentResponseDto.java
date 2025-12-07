package kr.adapterz.community.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import kr.adapterz.community.domain.post.entity.PostComment;
import kr.adapterz.community.domain.user.entity.User;

public record CommentResponseDto(
        Long id,
        String body,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime createdAt,
        UserDto user,
        Boolean isAuthor
) {
    public record UserDto(
            Long id,
            String nickname
    ) {
        public static UserDto from(User user) {
            return new UserDto(user.getId(), user.getNickname());
        }
    }

    public static CommentResponseDto of(PostComment comment, Long loggedInUserId) {
        boolean isAuthor = comment.getUser().getId().equals(loggedInUserId);
        return new CommentResponseDto(
                comment.getId(),
                comment.getBody(),
                comment.getCreatedAt(),
                UserDto.from(comment.getUser()),
                isAuthor
        );
    }
}
