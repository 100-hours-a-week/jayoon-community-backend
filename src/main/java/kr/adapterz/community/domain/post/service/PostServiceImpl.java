package kr.adapterz.community.domain.post.service;

import static kr.adapterz.community.common.message.ErrorCode.POST_NOT_FOUND;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.adapterz.community.common.exception.dto.NotFoundException;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostImageCreateDto;
import kr.adapterz.community.domain.post.dto.PostListResponseDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;
import kr.adapterz.community.domain.post.dto.PostSummaryResponseDto;
import kr.adapterz.community.domain.post.dto.PostUpdateRequestDto;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.entity.PostImage;
import kr.adapterz.community.domain.post.repository.PostImageRepository;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserService userService;

    /**
     * 게시물을 생성합니다.
     *
     * PostImage는 Post의 id가 필요한데, Post는 GenerationType.IDENTITY라서 저장을 해야만 id를 얻을 수 있다.
     *
     * @param request
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto request, Long userId) {
        User user = userService.findById(userId);
        Post newPost = Post.createFrom(user, request);

        Post savedPost = postRepository.save(newPost);
        List<PostImageCreateDto> images = createImage(savedPost.getId(), request.imageUrls());
        return PostResponseDto.of(savedPost, images, true);
    }

    /**
     * 특정 게시물의 상세 정보를 조회합니다.
     *
     * @param postId
     * @return
     */
    @Override
    public PostResponseDto findPostDetailById(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(
                POST_NOT_FOUND));
        List<PostImageCreateDto> postImages = findPostImageByPostId(postId);

        boolean isAuthor = (userId != null) && post.getUser().getId().equals(userId);

        return PostResponseDto.of(post, postImages, isAuthor);
    }

    /**
     * Cursor ID를 받아 해당하는 게시물 요약 목록을 가져옵니다.
     *
     * - (필수) limit: Number, 10
     *
     * - (선택) cursor: Number, 11
     * - 마지막으로 불러온 마지막 게시글 id
     * - 첫 번째 요청 때는 해당 파라미터를 생략합니다.
     * - 두 번째 요청 이후부터는 이전 응답의 nextCursor를 사용합니다.
     *
     * @return
     */
    @Override
    public PostListResponseDto findPostSummaries(Long limit, Long cursor) {
        Pageable pageable = PageRequest.of(0, limit.intValue());
        List<Post> posts;

        if (cursor == null) {
            posts = postRepository.findAll(
                            PageRequest.of(0, limit.intValue(), Sort.by(Sort.Direction.DESC, "id")))
                    .getContent();
        } else {
            posts = postRepository.findByIdLessThanOrderByIdDesc(cursor, pageable);
        }

        List<PostSummaryResponseDto> postSummaries = posts.stream()
                .map(PostSummaryResponseDto::from)
                .collect(Collectors.toList());

        Long nextCursor = null;
        if (!posts.isEmpty() && posts.size() == limit) {
            nextCursor = posts.get(posts.size() - 1).getId();
        }

        long totalCount = postRepository.count();

        return new PostListResponseDto(postSummaries, nextCursor, totalCount);
    }

    /**
     * 특정 게시물을 수정합니다.
     *
     * @param postId
     * @return
     */
    @Override
    @Transactional
    public PostResponseDto editPost(Long userId, Long postId, PostUpdateRequestDto request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new NotFoundException(POST_NOT_FOUND);
        }

        post.update(request);

        List<PostImageCreateDto> postImages = findPostImageByPostId(postId);
        return PostResponseDto.of(post, postImages, true);
    }

    /**
     * 특정 게시물을 삭제합니다.
     *
     * @param postId
     */
    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new NotFoundException(POST_NOT_FOUND);
        }

        postRepository.delete(post);
    }

    /**
     * 이미지를 생성합니다.
     *
     * 게시물 수정 시 이미지를 변경하는 것은 PostImage 입장에서 새로운 이미지를 생성하는 것입니다.
     * 생성만 하고 가지고 id는 줄 필요 없습니다. 클라이언트에서 필요한 정보는 URL입니다.
     *
     * @param postId
     * @param imageUrls
     */
    @Override
    @Transactional
    public List<PostImageCreateDto> createImage(Long postId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return Collections.emptyList();
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND));

        List<PostImage> postImages = imageUrls.stream()
                .map(url -> PostImage.of(post, url))
                .collect(Collectors.toList());

        List<PostImage> savedPostImages = postImageRepository.saveAll(postImages);

        return savedPostImages.stream()
                .map(image -> new PostImageCreateDto(image.getId(), image.getPostImageUrl()))
                .collect(Collectors.toList());
    }

    public List<PostImageCreateDto> findPostImageByPostId(Long postId) {
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        return postImages.stream()
                .map(image -> new PostImageCreateDto(image.getId(), image.getPostImageUrl()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteImage(Long postImageId) {
        // Will be implemented later
    }
}
