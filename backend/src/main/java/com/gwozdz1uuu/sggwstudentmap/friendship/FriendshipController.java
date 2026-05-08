package com.gwozdz1uuu.sggwstudentmap.friendship;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/friends")
@CrossOrigin
@Tag(name = "Znajomi", description = "Lista znajomych, zaproszenia, akceptacja")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<FriendshipResponse>> getFriends() {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(friendshipService.getFriends(userId));
    }

    @GetMapping("/pending/received")
    public ResponseEntity<List<FriendshipResponse>> getPendingReceived() {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(friendshipService.getPendingReceived(userId));
    }

    @GetMapping("/pending/sent")
    public ResponseEntity<List<FriendshipResponse>> getPendingSent() {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(friendshipService.getPendingSent(userId));
    }

    @GetMapping("/invitable")
    public ResponseEntity<List<UserSummary>> getInvitableUsers() {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(friendshipService.getInvitableUsers(userId));
    }

    @PostMapping("/invite")
    public ResponseEntity<FriendshipResponse> sendInvite(@RequestBody FriendshipRequest request) {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendshipService.sendInvite(userId, request.targetUserId()));
    }

    @PutMapping("/{senderId}/accept")
    public ResponseEntity<FriendshipResponse> acceptInvite(@PathVariable Integer senderId) {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(friendshipService.acceptInvite(userId, senderId));
    }

    @DeleteMapping("/{otherUserId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Integer otherUserId) {
        Integer userId = authService.getCurrentUser().getId();
        friendshipService.deleteFriendship(userId, otherUserId);
        return ResponseEntity.noContent().build();
    }
}
