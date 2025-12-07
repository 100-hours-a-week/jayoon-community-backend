package kr.adapterz.community.domain.post.service;

import java.util.List;
import kr.adapterz.community.domain.post.dto.PostCreateRequestDto;
import kr.adapterz.community.domain.post.dto.PostImageCreateDto;
import kr.adapterz.community.domain.post.dto.PostListResponseDto;
import kr.adapterz.community.domain.post.dto.PostResponseDto;

public interface PostService {

    // 게시물 생성
    PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId);

    // 게시물 상세 조회
    PostResponseDto findPostDetailById(Long postId, Long userId);

    // 게시물 목록 조회
    PostListResponseDto findPostSummaries(Long limit, Long cursor);

    // 게시물 수정
    PostResponseDto editPost(Long userId, Long postId);

    // 게시물 삭제
    void deletePost(Long userId, Long postId);

    // 게시물 이미지 생성
    List<PostImageCreateDto> createImage(Long postId, List<String> imageUrls);

    // 게시물 이미지 삭제
    void deleteImage(Long postImageId);
}
