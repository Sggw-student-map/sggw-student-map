package com.gwozdz1uuu.sggwstudentmap.friendship;

import com.gwozdz1uuu.sggwstudentmap.user.User;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // ---- Get accepted friends ----
    public List<FriendshipResponse> getFriends(Integer currentUserId) {
        return friendshipRepository.findAcceptedFriendships(currentUserId)
                .stream()
                .map(f -> toResponse(f, currentUserId))
                .toList();
    }

    // ---- Get pending received invites ----
    public List<FriendshipResponse> getPendingReceived(Integer currentUserId) {
        return friendshipRepository.findPendingReceived(currentUserId)
                .stream()
                .map(f -> toResponse(f, currentUserId))
                .toList();
    }

    // ---- Get pending sent invites ----
    public List<FriendshipResponse> getPendingSent(Integer currentUserId) {
        return friendshipRepository.findPendingSent(currentUserId)
                .stream()
                .map(f -> toResponse(f, currentUserId))
                .toList();
    }

    // ---- Users you can invite (no existing friendship) ----
    public List<UserSummary> getInvitableUsers(Integer currentUserId) {
        return friendshipRepository.findUsersNotInFriendship(currentUserId)
                .stream()
                .map(row -> new UserSummary(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3]
                ))
                .toList();
    }

    // ---- Send invite ----
    public FriendshipResponse sendInvite(Integer currentUserId, Integer targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself");
        }

        friendshipRepository.findBetween(currentUserId, targetUserId).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists");
        });

        Friendship f = new Friendship();
        f.setUserId1(currentUserId);
        f.setUserId2(targetUserId);
        f.setStatus("pending");
        return toResponse(friendshipRepository.save(f), currentUserId);
    }

    // ---- Accept invite ----
    public FriendshipResponse acceptInvite(Integer currentUserId, Integer senderUserId) {
        Friendship f = friendshipRepository.findBetween(senderUserId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite not found"));

        if (!f.getUserId2().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your invite to accept");
        }

        f.setStatus("accepted");
        return toResponse(friendshipRepository.save(f), currentUserId);
    }

    // ---- Reject or delete friendship ----
    public void deleteFriendship(Integer currentUserId, Integer otherUserId) {
        Friendship f = friendshipRepository.findBetween(currentUserId, otherUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found"));

        friendshipRepository.delete(f);
    }

    // ---- Helper ----
    private FriendshipResponse toResponse(Friendship f, Integer currentUserId) {
        User u1 = userRepository.findById(f.getUserId1())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        User u2 = userRepository.findById(f.getUserId2())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return FriendshipResponse.of(f, currentUserId,
                new UserSummary(u1.getId(), u1.getUsername(), u1.getFirstName(), u1.getLastName()),
                new UserSummary(u2.getId(), u2.getUsername(), u2.getFirstName(), u2.getLastName()));
    }
}
