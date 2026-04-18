package com.gwozdz1uuu.sggwstudentmap.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Getter @Setter
public class PostComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "id_post")    private Integer idPost;
    @Column(name = "id_user")    private Integer idUser;
    @Column(name = "content")    private String content;
    @Column(name = "created_at") private LocalDateTime createdAt;
}