package com.gwozdz1uuu.sggwstudentmap.event;

import java.time.LocalDateTime;

record CreateEventRequest(
        String nameOfEvent,
        Integer idPlace,
        LocalDateTime dateOfEvent,
        String comment
) {}

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
        Integer participantCount,
        boolean joinedByMe,
        boolean organizedByMe
) {}