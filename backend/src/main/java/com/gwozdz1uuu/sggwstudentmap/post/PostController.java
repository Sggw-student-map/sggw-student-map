package com.gwozdz1uuu.sggwstudentmap.post;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts(authService.getCurrentUser().getId()));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(authService.getCurrentUser().getId(), request));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Integer postId) {
        postService.likePost(authService.getCurrentUser().getId(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Integer postId) {
        postService.unlikePost(authService.getCurrentUser().getId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<PostCommentResponse>> getComments(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostCommentResponse> addComment(
            @PathVariable Integer postId,
            @RequestBody PostCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.addComment(authService.getCurrentUser().getId(), postId, request.content()));
    }
}