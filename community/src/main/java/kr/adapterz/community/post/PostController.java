package kr.adapterz.community.post;

import kr.adapterz.community.post.dto.CreatePostRequest;
import kr.adapterz.community.post.dto.PostResponse;
import kr.adapterz.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        PostResponse newPost = postService.createPost(request);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

//    @GetMapping
//    public ResponseEntity<List<PostResponse>> getPosts() {
//        List<PostResponse> posts = postService.findAllPosts();
//        return ResponseEntity.ok(posts);
//    }
//
//    @GetMapping("/{postId}")
//    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
//        PostResponse post = postService.findPostById(postId);
//        return ResponseEntity.ok(post);
//    }
//
//    @PutMapping("/{postId}")
//    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
//        PostResponse updatedPost = postService.updatePost(postId, request);
//        return ResponseEntity.ok(updatedPost);
//    }
//
//    @DeleteMapping("/{postId}")
//    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
//        postService.deletePost(postId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}