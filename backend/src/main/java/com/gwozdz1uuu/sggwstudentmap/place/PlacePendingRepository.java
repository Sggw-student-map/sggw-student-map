package com.gwozdz1uuu.sggwstudentmap.place;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlacePendingRepository extends JpaRepository<PlacePending, Integer> {
    List<PlacePending> findByStatusOrderByCreatedAtDesc(PendingStatus status);
}