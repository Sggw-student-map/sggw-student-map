package com.gwozdz1uuu.sggwstudentmap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recenzje")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recenzja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rec")
    private Integer idRec;

    @ManyToOne
    @JoinColumn(name = "id_miejsca", nullable = false)
    private Miejsce miejsce;

    @ManyToOne
    @JoinColumn(name = "id_uzytk", nullable = false)
    private Uzytkownik uzytkownik;

    @Column(name = "ocena")
    private Integer ocena;

    @Column(name = "komentarz")
    private String komentarz;
}
