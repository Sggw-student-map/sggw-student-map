package com.gwozdz1uuu.sggwstudentmap.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByPlaceId(Integer placeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.placeId = :placeId")
    Optional<Double> findAverageRatingByPlaceId(@Param("placeId") Integer placeId);
}
