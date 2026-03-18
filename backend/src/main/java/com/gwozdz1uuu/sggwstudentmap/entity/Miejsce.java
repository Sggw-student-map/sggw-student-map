package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "miejsca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Miejsce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miejsca")
    private Integer idMiejsca;

    @Column(name = "nazwa", nullable = false, length = 255)
    private String nazwa;

    @Column(name = "szer_geo", nullable = false)
    private Double szerGeo;

    @Column(name = "dl_geo", nullable = false)
    private Double dlGeo;
}