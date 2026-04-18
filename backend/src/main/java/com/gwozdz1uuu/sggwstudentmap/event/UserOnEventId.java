package com.gwozdz1uuu.sggwstudentmap.event;

import java.io.Serializable;
import java.util.Objects;

public class UserOnEventId implements Serializable {
    private Integer idEventu;
    private Integer idUsers;

    public UserOnEventId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOnEventId)) return false;
        UserOnEventId that = (UserOnEventId) o;
        return Objects.equals(idEventu, that.idEventu) && Objects.equals(idUsers, that.idUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEventu, idUsers);
    }
}