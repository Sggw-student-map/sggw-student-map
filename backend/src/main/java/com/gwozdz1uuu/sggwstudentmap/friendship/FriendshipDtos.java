package com.gwozdz1uuu.sggwstudentmap.friendship;

// ---- Request to send an invite ----
record FriendshipRequest(Integer targetUserId) {}

// ---- User summary shown in friend lists ----
record UserSummary(Integer id, String username, String firstName, String lastName) {}

// ---- Full friendship response ----
record FriendshipResponse(
        UserSummary user,       // the OTHER user (not the logged-in one)
        String status,
        String direction        // "sent" | "received" | "accepted"
) {
    static FriendshipResponse of(Friendship f, Integer currentUserId,
                                  UserSummary user1, UserSummary user2) {
        boolean currentIsUser1 = f.getUserId1().equals(currentUserId);
        UserSummary other = currentIsUser1 ? user2 : user1;

        String direction;
        if ("accepted".equals(f.getStatus())) {
            direction = "accepted";
        } else {
            direction = currentIsUser1 ? "sent" : "received";
        }

        return new FriendshipResponse(other, f.getStatus(), direction);
    }
}
