package com.gwozdz1uuu.sggwstudentmap.post;

import com.gwozdz1uuu.sggwstudentmap.place.PlaceRepository;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public List<PostResponse> getAllPosts(Integer currentUserId) {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> toResponse(p, currentUserId))
                .toList();
    }

    public PostResponse createPost(Integer currentUserId, CreatePostRequest request) {
        placeRepository.findById(request.idPlace())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));
        var post = new Post();
        post.setIdPlace(request.idPlace());
        post.setIdUser(currentUserId);
        post.setContent(request.content());
        post.setCreatedAt(LocalDateTime.now());
        return toResponse(postRepository.save(post), currentUserId);
    }

    public void likePost(Integer currentUserId, Integer postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!postLikeRepository.existsByIdPostAndIdUser(postId, currentUserId)) {
            var like = new PostLike();
            like.setIdPost(postId);
            like.setIdUser(currentUserId);
            postLikeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Integer currentUserId, Integer postId) {
        postLikeRepository.deleteByIdPostAndIdUser(postId, currentUserId);
    }

    public List<PostCommentResponse> getComments(Integer currentUserId, Integer postId) {
        return postCommentRepository.findByIdPostOrderByCreatedAt(postId).stream()
                .map(c -> toCommentResponse(c, currentUserId))
                .toList();
    }

    public PostCommentResponse addComment(Integer currentUserId, Integer postId, String content) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var comment = new PostComment();
        comment.setIdPost(postId);
        comment.setIdUser(currentUserId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return toCommentResponse(postCommentRepository.save(comment), currentUserId);
    }

    @Transactional
    public void deletePost(Integer currentUserId, Integer postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!post.getIdUser().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your post");
        }
        postRepository.delete(post);
    }

    public void deleteComment(Integer currentUserId, Integer commentId) {
        var comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getIdUser().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
        }
        postCommentRepository.delete(comment);
    }

    private PostResponse toResponse(Post p, Integer currentUserId) {
        var place = placeRepository.findById(p.getIdPlace()).orElse(null);
        var user = userRepository.findById(p.getIdUser()).orElse(null);
        return new PostResponse(
                p.getId(),
                p.getContent(),
                // p.getImageUrl(), // ← odkomentuj gdy chmura gotowa
                p.getCreatedAt().toString(),
                p.getIdPlace(),
                place != null ? place.getName() : "",
                p.getIdUser(),
                user != null ? user.getFirstName() : "",
                user != null ? user.getLastName() : "",
                (int) postLikeRepository.countByIdPost(p.getId()),
                postLikeRepository.existsByIdPostAndIdUser(p.getId(), currentUserId),
                (int) postCommentRepository.countByIdPost(p.getId()),
                p.getIdUser().equals(currentUserId)
        );
    }

    private PostCommentResponse toCommentResponse(PostComment c, Integer currentUserId) {
        var user = userRepository.findById(c.getIdUser()).orElse(null);
        return new PostCommentResponse(
                c.getId(), c.getContent(),
                c.getCreatedAt().toString(),
                c.getIdUser(),
                user != null ? user.getFirstName() : "",
                user != null ? user.getLastName() : "",
                c.getIdUser().equals(currentUserId)
        );
    }
}