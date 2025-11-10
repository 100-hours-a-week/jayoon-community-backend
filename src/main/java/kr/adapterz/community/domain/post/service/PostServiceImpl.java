package kr.adapterz.community.domain.post.service;

import static kr.adapterz.community.common.message.ErrorCode.POST_NOT_FOUND;

import java.util.List;
import kr.adapterz.community.common.exception.dto.NotFoundException;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostImageCreateDto;
import kr.adapterz.community.domain.post.dto.PostListResponseDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;
import kr.adapterz.community.domain.post.entity.Post;
import kr.adapterz.community.domain.post.entity.PostImage;
import kr.adapterz.community.domain.post.repository.PostImageRepository;
import kr.adapterz.community.domain.post.repository.PostRepository;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
    public PostResponseDto createPost(PostCreateRequestDto request, Long userId) {
        User user = userService.findById(userId);
        Post newPost = Post.createFrom(user, request);

        Post savedPost = postRepository.save(newPost);
        List<PostImageCreateDto> images = createImage(savedPost.getId(), request.imageUrls());
        return PostResponseDto.of(savedPost, images);
    }

    /**
     * 특정 게시물의 상세 정보를 조회합니다.
     *
     * @param postId
     * @return
     */
    @Override
    public PostResponseDto findPostDetailById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(
                POST_NOT_FOUND));
        List<PostImageCreateDto> postImages = findPostImageByPostId(postId);
        return PostResponseDto.of(post, postImages);
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
    public PostListResponseDto findPostSummaries(/*Long limit, Long cursor*/) {
        return null;
    }

    /**
     * 특정 게시물을 수정합니다.
     *
     * @param postId
     * @return
     */
    @Override
    public PostResponseDto editPost(Long userId, Long postId) {
        return null;
    }

    /**
     * 특정 게시물을 삭제합니다.
     *
     * @param postId
     */
    @Override
    public void deletePost(Long userId, Long postId) {

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
    public List<PostImageCreateDto> createImage(Long postId, List<String> imageUrls) {
        // post 존재 확인

        // imageUrls 돌아가면서 전부 저장
        return null;
    }

    public List<PostImageCreateDto> findPostImageByPostId(Long postId) {
        List<PostImage> postImage = postImageRepository.findAllByPostId(postId);
        return null;
    }

    @Override
    public void deleteImage(Long postImageId) {
        // 삭제
    }
}
