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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(userId, request));
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable Integer eventId) {
        Integer userId = authService.getCurrentUser().getId();
        eventService.joinEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/join")
    public ResponseEntity<Void> leaveEvent(@PathVariable Integer eventId) {
        Integer userId = authService.getCurrentUser().getId();
        eventService.leaveEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}