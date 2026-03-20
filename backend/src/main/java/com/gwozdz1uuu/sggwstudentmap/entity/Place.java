package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miejsca")
    private Integer id;

    @Column(name = "nazwa", nullable = false, length = 255)
    private String name;

    @Column(name = "szer_geo", nullable = false)
    private Double latitude;

    @Column(name = "dl_geo", nullable = false)
    private Double longitude;
}
