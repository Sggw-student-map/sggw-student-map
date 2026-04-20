package com.gwozdz1uuu.sggwstudentmap.feed;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedPostRepository extends JpaRepository<FeedPost, Integer> {

    @Query(value = """
        SELECT
            p.id               AS id,
            p.author_id        AS author_id,
            u.first_name       AS author_first_name,
            u.last_name        AS author_last_name,
            u.username         AS author_username,
            p.place_id         AS place_id,
            pl.name            AS place_name,
            p.content          AS content,
            p.image_url        AS image_url,
            p.created_at       AS created_at,
            (SELECT COUNT(*) FROM feed_post_likes l    WHERE l.post_id = p.id) AS likes_count,
            (SELECT COUNT(*) FROM feed_post_comments c WHERE c.post_id = p.id) AS comments_count,
            EXISTS (
                SELECT 1 FROM feed_post_likes l
                WHERE l.post_id = p.id AND l.user_id = :viewerId
            ) AS liked_by_me
        FROM feed_posts p
        JOIN users  u  ON u.id  = p.author_id
        LEFT JOIN places pl ON pl.id = p.place_id
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<FeedPostProjection> findFeed(@Param("viewerId") Integer viewerId, Pageable pageable);

    @Query(value = """
        SELECT
            p.id               AS id,
            p.author_id        AS author_id,
            u.first_name       AS author_first_name,
            u.last_name        AS author_last_name,
            u.username         AS author_username,
            p.place_id         AS place_id,
            pl.name            AS place_name,
            p.content          AS content,
            p.image_url        AS image_url,
            p.created_at       AS created_at,
            (SELECT COUNT(*) FROM feed_post_likes l    WHERE l.post_id = p.id) AS likes_count,
            (SELECT COUNT(*) FROM feed_post_comments c WHERE c.post_id = p.id) AS comments_count,
            EXISTS (
                SELECT 1 FROM feed_post_likes l
                WHERE l.post_id = p.id AND l.user_id = :viewerId
            ) AS liked_by_me
        FROM feed_posts p
        JOIN users  u  ON u.id  = p.author_id
        LEFT JOIN places pl ON pl.id = p.place_id
        WHERE p.id = :postId
        """, nativeQuery = true)
    FeedPostProjection findProjectionById(@Param("postId") Integer postId,
                                          @Param("viewerId") Integer viewerId);
}
