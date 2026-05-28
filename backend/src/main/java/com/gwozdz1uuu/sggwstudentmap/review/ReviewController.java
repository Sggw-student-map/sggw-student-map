package com.gwozdz1uuu.sggwstudentmap.review;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Recenzje", description = "Opinie o miejscach")
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

    @PostMapping(value = "/places/{placeId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Review> addReview(
            @PathVariable Integer placeId,
            @RequestPart("review") Review review,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(placeId, review, image));
    }
}
