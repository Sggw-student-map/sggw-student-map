package com.gwozdz1uuu.sggwstudentmap.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "event_interested")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EventInterestedId.class)
public class EventInterested {

    @Id
    @Column(name = "id_event")
    private Integer idEvent;

    @Id
    @Column(name = "id_user")
    private Integer idUser;
}

class EventInterestedId implements Serializable {
    private Integer idEvent;
    private Integer idUser;
    public EventInterestedId() {}
    @Override public boolean equals(Object o) {
        if (!(o instanceof EventInterestedId)) return false;
        EventInterestedId that = (EventInterestedId) o;
        return Objects.equals(idEvent, that.idEvent) && Objects.equals(idUser, that.idUser);
    }
    @Override public int hashCode() { return Objects.hash(idEvent, idUser); }
}