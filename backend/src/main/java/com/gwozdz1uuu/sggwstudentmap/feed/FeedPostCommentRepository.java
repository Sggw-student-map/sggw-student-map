package com.gwozdz1uuu.sggwstudentmap.feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedPostCommentRepository extends JpaRepository<FeedPostComment, Integer> {

    List<FeedPostComment> findByPostIdOrderByCreatedAtAsc(Integer postId);

    @Query(value = """
        SELECT
            c.id         AS id,
            c.post_id    AS post_id,
            c.author_id  AS author_id,
            u.first_name AS author_first_name,
            u.last_name  AS author_last_name,
            u.username   AS author_username,
            c.content    AS content,
            c.created_at AS created_at
        FROM feed_post_comments c
        JOIN users u ON u.id = c.author_id
        WHERE c.post_id = :postId
        ORDER BY c.created_at ASC
        """, nativeQuery = true)
    List<FeedCommentProjection> findCommentProjectionsByPostId(@Param("postId") Integer postId);

    interface FeedCommentProjection {
        Integer getId();
        Integer getPostId();
        Integer getAuthorId();
        String  getAuthorFirstName();
        String  getAuthorLastName();
        String  getAuthorUsername();
        String  getContent();
        LocalDateTime getCreatedAt();
    }
}
