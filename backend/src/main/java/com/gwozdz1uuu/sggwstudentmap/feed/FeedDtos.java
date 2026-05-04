package com.gwozdz1uuu.sggwstudentmap.feed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


final class FeedDtos {
    private FeedDtos() {}
}

record CreatePostRequest(
        Integer placeId,
        @NotBlank @Size(max = 4000) String content,
        String imageUrl
) {}

record CreateCommentRequest(
        @NotBlank @Size(max = 2000) String content
) {}

record FeedAuthor(
        Integer id,
        String username,
        String firstName,
        String lastName
) {}

record FeedPlace(
        Integer id,
        String name
) {}

record FeedPostResponse(
        Integer id,
        FeedAuthor author,
        FeedPlace place,
        String content,
        String imageUrl,
        LocalDateTime createdAt,
        long likesCount,
        long commentsCount,
        boolean likedByMe,
        boolean authoredByMe,
        boolean authorPrivateAccount
) {}

record FeedCommentResponse(
        Integer id,
        Integer postId,
        FeedAuthor author,
        String content,
        LocalDateTime createdAt,
        boolean authoredByMe
) {}
