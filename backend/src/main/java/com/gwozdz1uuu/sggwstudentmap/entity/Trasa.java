package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trasa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trasa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trasy")
    private Integer idTrasy;

    @ManyToOne
    @JoinColumn(name = "id_miejsca", nullable = false)
    private Miejsce miejsce;
}
