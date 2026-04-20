package com.gwozdz1uuu.sggwstudentmap.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_of_event", nullable = false, length = 255)
    private String nameOfEvent;

    @Column(name = "id_place", nullable = false)
    private Integer idPlace;

    @Column(name = "date_of_event")
    private LocalDateTime dateOfEvent;

    @Column(name = "comment")
    private String comment;

    @Column(name = "organizer_id")
    private Integer organizerId;
}