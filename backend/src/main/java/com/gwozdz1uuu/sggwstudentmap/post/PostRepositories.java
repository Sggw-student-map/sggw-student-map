package com.gwozdz1uuu.sggwstudentmap.post;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByOrderByCreatedAtDesc();
}

interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByIdPostAndIdUser(Integer idPost, Integer idUser);
    long countByIdPost(Integer idPost);
    void deleteByIdPostAndIdUser(Integer idPost, Integer idUser);
}

interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    List<PostComment> findByIdPostOrderByCreatedAt(Integer idPost);
    long countByIdPost(Integer idPost);
}