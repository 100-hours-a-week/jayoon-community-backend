package kr.adapterz.community.post.dto;

import kr.adapterz.community.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 조회/생성/수정 후 클라이언트에 반환할 응답 데이터 레코드
 */
@Builder
public record PostResponse(
        Long id,
        String title,
        String body,
        Long likeCount,
        Long commentCount,
        Long viewCount,
        List<String> imageUrls,
        UserResponse user,
        LocalDateTime createdAt
//        boolean isAuthor,
//        boolean isLiked
) {
    /**
     * 게시글 작성자 정보를 담는 내부 레코드
     */
    public record UserResponse(
            Long id,
            String nickname
    ) {
        public static UserResponse from(Long id,  String nickname) {
            return new UserResponse(id, nickname);
        }
    }

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
//                .imageUrls(post.getImageUrls())
                .user(UserResponse.from(post.getUser().getId(), post.getUser().getNickname()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}