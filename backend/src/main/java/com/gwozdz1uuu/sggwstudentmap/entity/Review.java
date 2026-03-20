package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rec")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_miejsca", nullable = false)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "id_uzytk", nullable = false)
    private User user;

    @Column(name = "ocena")
    private Integer rating;

    @Column(name = "komentarz")
    private String comment;
}
