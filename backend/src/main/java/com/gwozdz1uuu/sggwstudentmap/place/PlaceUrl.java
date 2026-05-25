package com.gwozdz1uuu.sggwstudentmap.place;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;
}