package kr.adapterz.community.domain.post.service;

import kr.adapterz.community.domain.post.dto.PostLikeCountResponseDto;

public interface LikeService {

    PostLikeCountResponseDto createLike(Long postId, Long userId);

    PostLikeCountResponseDto deleteLike(Long postId, Long userId);
}
