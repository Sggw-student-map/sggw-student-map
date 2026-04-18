package com.gwozdz1uuu.sggwstudentmap.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter @Setter
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "id_place")   private Integer idPlace;
    @Column(name = "id_user")    private Integer idUser;
    @Column(name = "content")    private String content;
    // @Column(name = "image_url") private String imageUrl; // ← odkomentuj gdy chmura gotowa
    @Column(name = "created_at") private LocalDateTime createdAt;
}