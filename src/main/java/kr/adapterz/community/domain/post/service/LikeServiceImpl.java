package kr.adapterz.community.domain.post.service;

import static kr.adapterz.community.common.message.ErrorCode.POST_NOT_FOUND;

import kr.adapterz.community.common.exception.dto.NotFoundException;
import kr.adapterz.community.domain.post.dto.PostLikeCountResponseDto;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.entity.PostLike;
import kr.adapterz.community.domain.post.repository.PostLikeRepository;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeRepository postLikeRepository;

    /**
     * 좋아요를 생성합니다.
     *
     * 기존에 좋아요가 없다면 좋아요를 생성하고, post의 likeCount를 증가 시킵니다.
     *
     * @param postId
     * @param userId
     * @return
     */
    @Override
    public PostLikeCountResponseDto createLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));
        User user = userService.findById(userId);

        if (!postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
        }

        return new PostLikeCountResponseDto(post.getId(), post.getLikeCount());
    }

    /**
     * 좋아요를 삭제합니다.
     *
     * 기존에 좋아요가 존재한다면 좋아요를 삭제하고, post의 likeCount를 감소 시킵니다.
     *
     * @param postId
     * @param userId
     * @return
     */
    @Override
    public PostLikeCountResponseDto deleteLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));

        postLikeRepository.findByUserIdAndPostId(userId, postId).ifPresent(postLike -> {
            postLikeRepository.delete(postLike);
            post.decrementLikeCount();
        });

        return new PostLikeCountResponseDto(post.getId(), post.getLikeCount());
    }
}
