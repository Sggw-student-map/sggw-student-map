package com.gwozdz1uuu.sggwstudentmap.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_event", nullable = false)
    private Integer idEvent;

    @Column(name = "id_user", nullable = false)
    private Integer idUser;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}