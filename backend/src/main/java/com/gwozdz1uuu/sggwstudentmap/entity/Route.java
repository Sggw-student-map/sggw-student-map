package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trasy")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_miejsca", nullable = false)
    private Place place;
}
