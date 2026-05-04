package com.gwozdz1uuu.sggwstudentmap.review;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@CrossOrigin
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/reviews/my")
    public ResponseEntity<List<Review>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }

    @GetMapping("/places/{placeId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByPlace(@PathVariable Integer placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }

    @PostMapping("/places/{placeId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable Integer placeId, @RequestBody Review review) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(placeId, review));
    }
}
