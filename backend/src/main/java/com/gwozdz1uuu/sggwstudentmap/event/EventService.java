package com.gwozdz1uuu.sggwstudentmap.event;

import com.gwozdz1uuu.sggwstudentmap.place.Place;
import com.gwozdz1uuu.sggwstudentmap.place.PlaceRepository;
import com.gwozdz1uuu.sggwstudentmap.user.User;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserOnEventRepository userOnEventRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public List<EventResponse> getAllEvents(Integer currentUserId) {
        return eventRepository.findAllOrderByDate()
                .stream()
                .map(e -> toResponse(e, currentUserId))
                .toList();
    }

    public EventResponse createEvent(Integer currentUserId, CreateEventRequest request) {
        placeRepository.findById(request.idPlace())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));

        Event event = new Event();
        event.setNameOfEvent(request.nameOfEvent());
        event.setIdPlace(request.idPlace());
        event.setDateOfEvent(request.dateOfEvent());
        event.setComment(request.comment());
        event.setOrganizerId(currentUserId);

        return toResponse(eventRepository.save(event), currentUserId);
    }

    public void joinEvent(Integer currentUserId, Integer eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        userOnEventRepository.findByIdEventuAndIdUsers(eventId, currentUserId).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already joined");
        });
        userOnEventRepository.save(new UserOnEvent(eventId, currentUserId));
    }

    public void leaveEvent(Integer currentUserId, Integer eventId) {
        UserOnEvent uoe = userOnEventRepository.findByIdEventuAndIdUsers(eventId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not joined"));
        userOnEventRepository.delete(uoe);
    }

    private EventResponse toResponse(Event e, Integer currentUserId) {
        Place place = placeRepository.findById(e.getIdPlace())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));
        User organizer = e.getOrganizerId() != null
                ? userRepository.findById(e.getOrganizerId()).orElse(null)
                : null;

        int participantCount = userOnEventRepository.countByIdEventu(e.getId());
        boolean joinedByMe = userOnEventRepository.findByIdEventuAndIdUsers(e.getId(), currentUserId).isPresent();
        boolean organizedByMe = e.getOrganizerId() != null && e.getOrganizerId().equals(currentUserId);

        return new EventResponse(
                e.getId(),
                e.getNameOfEvent(),
                e.getDateOfEvent() != null ? e.getDateOfEvent().toString() : null,
                e.getComment(),
                place.getId(),
                place.getName(),
                // place.getImageUrl(), // ← odkomentuj gdy places będą miały image_url
                organizer != null ? organizer.getId() : null,
                organizer != null ? organizer.getFirstName() : null,
                organizer != null ? organizer.getLastName() : null,
                participantCount,
                joinedByMe,
                organizedByMe
        );
    }
}