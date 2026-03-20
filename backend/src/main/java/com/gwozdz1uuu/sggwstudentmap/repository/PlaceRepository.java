package com.gwozdz1uuu.sggwstudentmap.repository;

import com.gwozdz1uuu.sggwstudentmap.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
}
