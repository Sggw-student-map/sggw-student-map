package com.gwozdz1uuu.sggwstudentmap.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "event_likes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EventLikeId.class)
public class EventLike {

    @Id
    @Column(name = "id_event")
    private Integer idEvent;

    @Id
    @Column(name = "id_user")
    private Integer idUser;
}

class EventLikeId implements Serializable {
    private Integer idEvent;
    private Integer idUser;
    public EventLikeId() {}
    @Override public boolean equals(Object o) {
        if (!(o instanceof EventLikeId)) return false;
        EventLikeId that = (EventLikeId) o;
        return Objects.equals(idEvent, that.idEvent) && Objects.equals(idUser, that.idUser);
    }
    @Override public int hashCode() { return Objects.hash(idEvent, idUser); }
}