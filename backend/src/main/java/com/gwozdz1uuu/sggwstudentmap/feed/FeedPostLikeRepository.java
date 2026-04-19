package com.gwozdz1uuu.sggwstudentmap.feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedPostLikeRepository extends JpaRepository<FeedPostLike, FeedPostLikeId> {
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    void deleteByPostIdAndUserId(Integer postId, Integer userId);
    long countByPostId(Integer postId);
}
