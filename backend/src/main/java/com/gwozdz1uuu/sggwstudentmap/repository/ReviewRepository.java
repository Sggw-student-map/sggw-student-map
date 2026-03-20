package com.gwozdz1uuu.sggwstudentmap.repository;

import com.gwozdz1uuu.sggwstudentmap.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
