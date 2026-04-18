package com.gwozdz1uuu.sggwstudentmap.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e ORDER BY e.dateOfEvent ASC")
    List<Event> findAllOrderByDate();
}