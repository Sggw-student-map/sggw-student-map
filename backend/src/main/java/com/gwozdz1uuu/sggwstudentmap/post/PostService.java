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
        // post.setImageUrl(request.imageUrl()); // ← odkomentuj gdy chmura gotowa
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

    public List<PostCommentResponse> getComments(Integer postId) {
        return postCommentRepository.findByIdPostOrderByCreatedAt(postId).stream()
                .map(c -> toCommentResponse(c))
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
        return toCommentResponse(postCommentRepository.save(comment));
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
                (int) postCommentRepository.countByIdPost(p.getId())
        );
    }

    private PostCommentResponse toCommentResponse(PostComment c) {
        var user = userRepository.findById(c.getIdUser()).orElse(null);
        return new PostCommentResponse(
                c.getId(), c.getContent(),
                c.getCreatedAt().toString(),
                c.getIdUser(),
                user != null ? user.getFirstName() : "",
                user != null ? user.getLastName() : ""
        );
    }
}