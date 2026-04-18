package com.gwozdz1uuu.sggwstudentmap.event;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {

    private final EventService eventService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(eventService.getAllEvents(userId));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request) {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(userId, request));
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable Integer eventId) {
        eventService.joinEvent(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/join")
    public ResponseEntity<Void> leaveEvent(@PathVariable Integer eventId) {
        eventService.leaveEvent(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable Integer eventId) {
        eventService.likeEvent(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<Void> unlikeEvent(@PathVariable Integer eventId) {
        eventService.unlikeEvent(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/interested")
    public ResponseEntity<Void> markInterested(@PathVariable Integer eventId) {
        eventService.markInterested(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/interested")
    public ResponseEntity<Void> unmarkInterested(@PathVariable Integer eventId) {
        eventService.unmarkInterested(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer eventId) {
        return ResponseEntity.ok(eventService.getComments(eventId));
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Integer eventId,
                                                       @RequestBody CommentRequest request) {
        Integer userId = authService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addComment(userId, eventId, request));
    }

}