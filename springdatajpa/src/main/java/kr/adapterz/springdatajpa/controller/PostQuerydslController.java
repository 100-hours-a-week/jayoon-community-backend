package kr.adapterz.springdatajpa.controller;

import kr.adapterz.springdatajpa.service.PostQuerydslService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/qdsl/posts")
@RequiredArgsConstructor
public class PostQuerydslController {

    private final PostQuerydslService postQuerydslService;

    @GetMapping("/count")
    public Long countAllPostsWithCustomAlias() {
        return postQuerydslService.countAllPostsWithStaticImport();
    }
}