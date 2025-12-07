package kr.adapterz.community.domain.post.dto;

import kr.adapterz.community.domain.post.entity.Post;
import java.time.LocalDateTime;

/**
 * 게시물 목록 응답 DTO입니다.
 */
public record PostSummaryResponseDto(
        Long id,
        String title,
        Long likeCount,
        Long commentCount,
        Long viewCount,
        LocalDateTime createdAt,
        UserDto user
) {
    public record UserDto(
            Long id,
            String nickname,
            String profileImageUrl
    ) {
        public static UserDto of(Long id, String nickname, String profileImageUrl) {
            return new UserDto(id, nickname, profileImageUrl);
        }
    }

    public static PostSummaryResponseDto from(Post post) {
        return new PostSummaryResponseDto(
                post.getId(),
                post.getTitle(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getViewCount(),
                post.getCreatedAt(),
                UserDto.of(post.getUser().getId(), post.getUser().getNickname(), post.getUser().getProfileImageUrl())
        );
    }
    /**
     *  "data": {
     *     "posts": [
     *       {
     *         "id": 1,
     *         "title": "제목 1",
     *         "likeCount": 1,
     *         "commentCount": 1,
     *         "viewCount": 1,
     *         "createdAt": "1997-01-01T00:00:00.000Z",
     *         "user": {
     *           "id": 1,
     *           "nickname": "jayoon",
     *           "profileImageUrl": "url"
     *         }
     *       },
     *       // limit 개수만큼의 데이터
     *       {
     *         "id": 10,
     *         "title": "제목 10",
     *         "likeCount": 1,
     *         "commentCount": 1,
     *         "viewCount": 1,
     *         "createdAt": "1997-01-01T00:00:00.000Z",
     *         "user": {
     *           "id": 1,
     *           "nickname": "jayoon",
     *           "profileImageUrl": "url"
     *         }
     *       }
     *     ],
     *     "nextCursor": 10
     *   },
     */
}
