package com.gwozdz1uuu.sggwstudentmap.post;

record CreatePostRequest(
    Integer idPlace,
    String content
    // String imageUrl // ← odkomentuj gdy chmura gotowa
) {}

record PostResponse(
    Integer id,
    String content,
    // String imageUrl, // ← odkomentuj gdy chmura gotowa
    String createdAt,
    Integer placeId,
    String placeName,
    Integer authorId,
    String authorFirstName,
    String authorLastName,
    int likesCount,
    boolean likedByMe,
    int commentsCount
) {}

record PostCommentRequest(String content) {}

record PostCommentResponse(
    Integer id,
    String content,
    String createdAt,
    Integer authorId,
    String authorFirstName,
    String authorLastName
) {}