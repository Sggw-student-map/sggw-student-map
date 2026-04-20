package com.gwozdz1uuu.sggwstudentmap.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e ORDER BY e.dateOfEvent ASC")
    List<Event> findAllOrderByDate();
}

@Repository
interface EventLikeRepository extends JpaRepository<EventLike, EventLikeId> {
    Optional<EventLike> findByIdEventAndIdUser(Integer idEvent, Integer idUser);
    int countByIdEvent(Integer idEvent);
}

@Repository
interface EventInterestedRepository extends JpaRepository<EventInterested, EventInterestedId> {
    Optional<EventInterested> findByIdEventAndIdUser(Integer idEvent, Integer idUser);
    int countByIdEvent(Integer idEvent);
}

@Repository
interface EventCommentRepository extends JpaRepository<EventComment, Integer> {
    List<EventComment> findByIdEventOrderByCreatedAtAsc(Integer idEvent);
    int countByIdEvent(Integer idEvent);
}