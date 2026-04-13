package com.gwozdz1uuu.sggwstudentmap.friendship;

import java.io.Serializable;
import java.util.Objects;

public class FriendshipId implements Serializable {

    private Integer userId1;
    private Integer userId2;

    public FriendshipId() {}

    public FriendshipId(Integer userId1, Integer userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendshipId)) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(userId1, that.userId1) && Objects.equals(userId2, that.userId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId1, userId2);
    }
}
