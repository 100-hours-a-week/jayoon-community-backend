package kr.adapterz.community.domain.post.service;

import static kr.adapterz.community.common.message.ErrorCode.POST_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;
import kr.adapterz.community.common.exception.dto.NotFoundException;
import kr.adapterz.community.domain.post.dto.CommentCreateRequestDto;
import kr.adapterz.community.domain.post.dto.CommentListResponseDto;
import kr.adapterz.community.domain.post.dto.CommentResponseDto;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.entity.PostComment;
import kr.adapterz.community.domain.post.repository.PostCommentRepository;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto request) {
        User user = userService.findById(userId);
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));

        PostComment newComment = PostComment.of(user, post, request.body());
        PostComment savedComment = postCommentRepository.save(newComment);

        return CommentResponseDto.of(savedComment, userId);
    }

    @Override
    public CommentListResponseDto findCommentsByPostId(Long postId, Long limit, Long cursor, Long loggedInUserId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException(POST_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, limit.intValue());
        List<PostComment> comments;

        if (cursor == null) {
            comments = postCommentRepository.findByPostIdOrderByIdDesc(postId, pageable);
        } else {
            comments = postCommentRepository.findByPostIdAndIdLessThanOrderByIdDesc(postId, cursor,
                pageable);
        }

        List<CommentResponseDto> commentDtos = comments.stream()
            .map(comment -> CommentResponseDto.of(comment, loggedInUserId))
            .collect(Collectors.toList());

        Long nextCursor = null;
        if (!comments.isEmpty() && comments.size() == limit) {
            nextCursor = comments.get(comments.size() - 1).getId();
        }

        long totalCount = postCommentRepository.countByPostId(postId);

        return new CommentListResponseDto(commentDtos, nextCursor, totalCount);
    }
}
