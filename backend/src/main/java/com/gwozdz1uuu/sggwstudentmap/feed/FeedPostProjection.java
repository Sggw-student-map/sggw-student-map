package com.gwozdz1uuu.sggwstudentmap.feed;

import java.time.LocalDateTime;

/**
 * Read-side projection for the feed listing. Spring Data maps native-query
 * column aliases to getters by name.
 */
public interface FeedPostProjection {
    Integer getId();
    Integer getAuthorId();
    String  getAuthorFirstName();
    String  getAuthorLastName();
    String  getAuthorUsername();
    Integer getPlaceId();
    String  getPlaceName();
    String  getContent();
    String  getImageUrl();
    LocalDateTime getCreatedAt();
    Long    getLikesCount();
    Long    getCommentsCount();
    Boolean getLikedByMe();

    /** {@code true} gdy autor ma włączone prywatne konto (post widoczny tylko dla znajomych). */
    Boolean getAuthorPrivateAccount();
}
