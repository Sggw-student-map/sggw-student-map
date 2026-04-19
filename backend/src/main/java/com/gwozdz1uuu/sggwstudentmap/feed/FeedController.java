package com.gwozdz1uuu.sggwstudentmap.feed;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@AllArgsConstructor
@CrossOrigin
public class FeedController {

    private final FeedService feedService;

    // -------- Posts --------

    @GetMapping
    public ResponseEntity<List<FeedPostResponse>> getFeed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(feedService.getFeed(page, size));
    }

    @PostMapping
    public ResponseEntity<FeedPostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createPost(request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        feedService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // -------- Likes --------

    @PostMapping("/{postId}/like")
    public ResponseEntity<FeedPostResponse> like(@PathVariable Integer postId) {
        return ResponseEntity.ok(feedService.like(postId));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<FeedPostResponse> unlike(@PathVariable Integer postId) {
        return ResponseEntity.ok(feedService.unlike(postId));
    }

    // -------- Comments --------

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<FeedCommentResponse>> getComments(@PathVariable Integer postId) {
        return ResponseEntity.ok(feedService.getComments(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<FeedCommentResponse> addComment(@PathVariable Integer postId,
                                                          @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.addComment(postId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        feedService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
