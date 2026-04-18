package com.gwozdz1uuu.sggwstudentmap.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users_on_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserOnEventId.class)
public class UserOnEvent {

    @Id
    @Column(name = "id_eventu")
    private Integer idEventu;

    @Id
    @Column(name = "id_users")
    private Integer idUsers;
}