package com.gwozdz1uuu.sggwstudentmap.event;

import com.gwozdz1uuu.sggwstudentmap.place.Place;
import com.gwozdz1uuu.sggwstudentmap.place.PlaceRepository;
import com.gwozdz1uuu.sggwstudentmap.user.User;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserOnEventRepository userOnEventRepository;
    private final EventLikeRepository eventLikeRepository;
    private final EventInterestedRepository eventInterestedRepository;
    private final EventCommentRepository eventCommentRepository;
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

    public void likeEvent(Integer currentUserId, Integer eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventLikeRepository.findByIdEventAndIdUser(eventId, currentUserId).ifPresent(l -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already liked");
        });
        eventLikeRepository.save(new EventLike(eventId, currentUserId));
    }

    public void unlikeEvent(Integer currentUserId, Integer eventId) {
        EventLike like = eventLikeRepository.findByIdEventAndIdUser(eventId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found"));
        eventLikeRepository.delete(like);
    }

    public void markInterested(Integer currentUserId, Integer eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventInterestedRepository.findByIdEventAndIdUser(eventId, currentUserId).ifPresent(i -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already interested");
        });
        eventInterestedRepository.save(new EventInterested(eventId, currentUserId));
    }

    public void unmarkInterested(Integer currentUserId, Integer eventId) {
        EventInterested interested = eventInterestedRepository.findByIdEventAndIdUser(eventId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not interested"));
        eventInterestedRepository.delete(interested);
    }

    public CommentResponse addComment(Integer currentUserId, Integer eventId, CommentRequest request) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        EventComment comment = new EventComment();
        comment.setIdEvent(eventId);
        comment.setIdUser(currentUserId);
        comment.setContent(request.content());
        EventComment saved = eventCommentRepository.save(comment);
        return toCommentResponse(saved, author, currentUserId);
    }

    public List<CommentResponse> getComments(Integer currentUserId, Integer eventId) {
        return eventCommentRepository.findByIdEventOrderByCreatedAtAsc(eventId)
                .stream()
                .map(c -> {
                    User author = userRepository.findById(c.getIdUser()).orElse(null);
                    return toCommentResponse(c, author, currentUserId);
                })
                .toList();
    }

    private CommentResponse toCommentResponse(EventComment c, User author, Integer currentUserId) {
        return new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getCreatedAt() != null ? c.getCreatedAt().toString() : null,
                author != null ? author.getId() : null,
                author != null ? author.getFirstName() : null,
                author != null ? author.getLastName() : null,
                c.getIdUser().equals(currentUserId)
        );
    }

    private EventResponse toResponse(Event e, Integer currentUserId) {
        Place place = placeRepository.findById(e.getIdPlace())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));
        User organizer = e.getOrganizerId() != null
                ? userRepository.findById(e.getOrganizerId()).orElse(null)
                : null;
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
                userOnEventRepository.countByIdEventu(e.getId()),
                userOnEventRepository.findByIdEventuAndIdUsers(e.getId(), currentUserId).isPresent(),
                e.getOrganizerId() != null && e.getOrganizerId().equals(currentUserId),
                eventLikeRepository.countByIdEvent(e.getId()),
                eventLikeRepository.findByIdEventAndIdUser(e.getId(), currentUserId).isPresent(),
                eventInterestedRepository.countByIdEvent(e.getId()),
                eventInterestedRepository.findByIdEventAndIdUser(e.getId(), currentUserId).isPresent(),
                eventCommentRepository.countByIdEvent(e.getId())
        );
    }

    @Transactional
    public void deleteEvent(Integer currentUserId, Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (event.getOrganizerId() == null || !event.getOrganizerId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your event");
        }
        eventRepository.delete(event);
    }

    public void deleteComment(Integer currentUserId, Integer commentId) {
        EventComment comment = eventCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getIdUser().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
        }
        eventCommentRepository.delete(comment);
    }
}