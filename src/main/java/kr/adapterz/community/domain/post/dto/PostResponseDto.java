package kr.adapterz.community.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import kr.adapterz.community.domain.post.entity.Post;
import lombok.Builder;

/**
 * 게시글 조회/생성/수정 후 클라이언트에 반환할 응답 데이터 레코드
 */
@Builder
public record PostResponseDto(
        Long id,
        String title,
        String body,
        Long likeCount,
        Long commentCount,
        Long viewCount,
        List<PostImageCreateDto> imageUrls,
        LocalDateTime createdAt,
        UserResponse user,
        boolean isAuthor,
        boolean isLiked
) {
    /**
     * 게시글 작성자 정보를 담는 내부 레코드
     */
    public record UserResponse(
            Long id,
            String nickname
    ) {
        public static UserResponse of(Long id, String nickname) {
            return new UserResponse(id, nickname);
        }
    }

    public static PostResponseDto of(Post post, List<PostImageCreateDto> images, boolean isAuthor, boolean isLiked) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .imageUrls(images)
                .user(UserResponse.of(post.getUser().getId(), post.getUser().getNickname()))
                .createdAt(post.getCreatedAt())
                .isAuthor(isAuthor)
                .isLiked(isLiked)
                .build();
    }
}