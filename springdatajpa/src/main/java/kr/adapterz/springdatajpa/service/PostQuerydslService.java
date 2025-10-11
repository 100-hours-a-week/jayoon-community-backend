package kr.adapterz.springdatajpa.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.adapterz.springdatajpa.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.adapterz.springdatajpa.entity.QPost.post;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQuerydslService {
    private final JPAQueryFactory queryFactory;

    public Long countAllPostsWithCustomAlias() {
        QPost post = new QPost("post");
        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }

    public Long countAllPostsWithDefaultAlias() {
        QPost post = QPost.post;
        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }

    // static import 방식을 권장함
    public Long countAllPostsWithStaticImport() {
        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }
}
