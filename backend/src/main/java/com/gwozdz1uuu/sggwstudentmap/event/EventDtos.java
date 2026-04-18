package com.gwozdz1uuu.sggwstudentmap.event;

import java.time.LocalDateTime;

record CreateEventRequest(String nameOfEvent, Integer idPlace, LocalDateTime dateOfEvent, String comment) {}

record EventResponse(
        Integer id,
        String nameOfEvent,
        String dateOfEvent,
        String comment,
        Integer placeId,
        String placeName,
        // String placeImageUrl, // ← odkomentuj gdy places będą miały image_url
        Integer organizerId,
        String organizerFirstName,
        String organizerLastName,
        long participantCount,
        boolean joinedByMe,
        boolean organizedByMe,
        long likesCount,
        boolean likedByMe,
        long interestedCount,
        boolean interestedByMe,
        long commentsCount
) {}

record CommentRequest(String content) {}

record CommentResponse(
        Integer id,
        String content,
        String createdAt,
        Integer authorId,
        String authorFirstName,
        String authorLastName,
        boolean isMyComment
) {}