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
        return ResponseEntity.ok(eventService.getAllEvents(authService.getCurrentUser().getId()));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(authService.getCurrentUser().getId(), request));
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
        return ResponseEntity.ok(eventService.getComments(authService.getCurrentUser().getId(), eventId));
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Integer eventId,
                                                       @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.addComment(authService.getCurrentUser().getId(), eventId, request));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        eventService.deleteEvent(authService.getCurrentUser().getId(), eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer eventId,
                                               @PathVariable Integer commentId) {
        eventService.deleteComment(authService.getCurrentUser().getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}