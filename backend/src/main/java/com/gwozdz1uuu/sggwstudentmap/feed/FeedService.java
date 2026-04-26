package com.gwozdz1uuu.sggwstudentmap.feed;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import com.gwozdz1uuu.sggwstudentmap.friendship.FriendshipRepository;
import com.gwozdz1uuu.sggwstudentmap.place.PlaceRepository;
import com.gwozdz1uuu.sggwstudentmap.settings.UserSettingsRepository;
import com.gwozdz1uuu.sggwstudentmap.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedService {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;

    private final FeedPostRepository postRepository;
    private final FeedPostLikeRepository likeRepository;
    private final FeedPostCommentRepository commentRepository;
    private final PlaceRepository placeRepository;
    private final AuthService authService;
    private final UserSettingsRepository userSettingsRepository;
    private final FriendshipRepository friendshipRepository;

    public List<FeedPostResponse> getFeed(int page, int size) {
        Integer viewerId = currentUserIdOrZero();
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        return postRepository.findFeed(viewerId, pageable).stream()
                .filter(p -> canViewPost(p.getAuthorId(), viewerId))
                .map(p -> toResponse(p, viewerId))
                .toList();
    }

    @Transactional
    public FeedPostResponse createPost(CreatePostRequest request) {
        User me = requireCurrentUser();

        if (request.placeId() != null && !placeRepository.existsById(request.placeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Place not found");
        }

        FeedPost saved = postRepository.save(FeedPost.builder()
                .authorId(me.getId())
                .placeId(request.placeId())
                .content(request.content().trim())
                .imageUrl(emptyToNull(request.imageUrl()))
                .build());

        FeedPostProjection projection = postRepository.findProjectionById(saved.getId(), me.getId());
        return toResponse(projection, me.getId());
    }

    @Transactional
    public void deletePost(Integer postId) {
        User me = requireCurrentUser();
        FeedPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthorId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete somebody else's post");
        }
        postRepository.delete(post);
    }

    @Transactional
    public FeedPostResponse like(Integer postId) {
        User me = requireCurrentUser();
        FeedPost post = getPostOrThrow(postId);
        
        if (!canViewPost(post.getAuthorId(), me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access this post");
        }

        if (!likeRepository.existsByPostIdAndUserId(postId, me.getId())) {
            FeedPostLike like = new FeedPostLike();
            like.setPostId(postId);
            like.setUserId(me.getId());
            likeRepository.save(like);
        }

        return toResponse(postRepository.findProjectionById(postId, me.getId()), me.getId());
    }

    @Transactional
    public FeedPostResponse unlike(Integer postId) {
        User me = requireCurrentUser();
        FeedPost post = getPostOrThrow(postId);
        
        if (!canViewPost(post.getAuthorId(), me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access this post");
        }

        if (likeRepository.existsByPostIdAndUserId(postId, me.getId())) {
            likeRepository.deleteByPostIdAndUserId(postId, me.getId());
        }
        return toResponse(postRepository.findProjectionById(postId, me.getId()), me.getId());
    }

    public List<FeedCommentResponse> getComments(Integer postId) {
        FeedPost post = getPostOrThrow(postId);
        Integer viewerId = currentUserIdOrZero();
        
        if (!canViewPost(post.getAuthorId(), viewerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access this post");
        }

        return commentRepository.findCommentProjectionsByPostId(postId).stream()
                .map(c -> new FeedCommentResponse(
                        c.getId(),
                        c.getPostId(),
                        new FeedAuthor(
                                c.getAuthorId(),
                                c.getAuthorUsername(),
                                c.getAuthorFirstName(),
                                c.getAuthorLastName()
                        ),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getAuthorId().equals(viewerId)
                ))
                .toList();
    }

    @Transactional
    public FeedCommentResponse addComment(Integer postId, CreateCommentRequest request) {
        User me = requireCurrentUser();
        FeedPost post = getPostOrThrow(postId);
        
        if (!canViewPost(post.getAuthorId(), me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access this post");
        }

        FeedPostComment saved = commentRepository.save(FeedPostComment.builder()
                .postId(postId)
                .authorId(me.getId())
                .content(request.content().trim())
                .build());

        return new FeedCommentResponse(
                saved.getId(),
                saved.getPostId(),
                new FeedAuthor(me.getId(), me.getUsername(), me.getFirstName(), me.getLastName()),
                saved.getContent(),
                saved.getCreatedAt(),
                true
        );
    }

    @Transactional
    public void deleteComment(Integer commentId) {
        User me = requireCurrentUser();
        FeedPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        boolean isCommentAuthor = comment.getAuthorId().equals(me.getId());
        boolean isPostAuthor = postRepository.findById(comment.getPostId())
                .map(p -> p.getAuthorId().equals(me.getId()))
                .orElse(false);

        if (!isCommentAuthor && !isPostAuthor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete this comment");
        }
        commentRepository.delete(comment);
    }

    private FeedPostResponse toResponse(FeedPostProjection p, Integer viewerId) {
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        FeedPlace place = (p.getPlaceId() == null)
                ? null
                : new FeedPlace(p.getPlaceId(), p.getPlaceName());

        FeedAuthor author = new FeedAuthor(
                p.getAuthorId(),
                p.getAuthorUsername(),
                p.getAuthorFirstName(),
                p.getAuthorLastName()
        );

        long likes = p.getLikesCount() == null ? 0 : p.getLikesCount();
        long comments = p.getCommentsCount() == null ? 0 : p.getCommentsCount();
        boolean liked = Boolean.TRUE.equals(p.getLikedByMe());
        boolean authored = viewerId != null && viewerId > 0 && viewerId.equals(p.getAuthorId());

        return new FeedPostResponse(
                p.getId(),
                author,
                place,
                p.getContent(),
                p.getImageUrl(),
                p.getCreatedAt(),
                likes,
                comments,
                liked,
                authored
        );
    }

    private User requireCurrentUser() {
        User me = authService.getCurrentUser();
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return me;
    }

    private Integer currentUserIdOrZero() {
        User me = authService.getCurrentUser();
        return me == null ? 0 : me.getId();
    }

    private FeedPost getPostOrThrow(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private static String emptyToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean canViewPost(Integer authorId, Integer viewerId) {
        // Edge case: post bez autora
        if (authorId == null) {
            return true;
        }

        boolean isPrivate = userSettingsRepository.findById(authorId)
                .map(s -> Boolean.TRUE.equals(s.getPrivateAccount()))
                .orElse(false);

        if (!isPrivate) {
            return true;
        }
        
        if (viewerId == null || viewerId == 0) {
            return false;
        }

        if (authorId.equals(viewerId)) {
            return true;
        }
        
        return friendshipRepository.areAcceptedFriends(authorId, viewerId);
    }
}