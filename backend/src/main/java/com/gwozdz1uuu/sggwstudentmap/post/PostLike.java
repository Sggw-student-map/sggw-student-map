package com.gwozdz1uuu.sggwstudentmap.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "post_likes")
@IdClass(PostLikeId.class)
@Getter @Setter
public class PostLike {
    @Id @Column(name = "id_post") private Integer idPost;
    @Id @Column(name = "id_user") private Integer idUser;
}

class PostLikeId implements Serializable {
    private Integer idPost;
    private Integer idUser;
}