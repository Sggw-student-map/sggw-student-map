package com.gwozdz1uuu.sggwstudentmap.review;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import com.gwozdz1uuu.sggwstudentmap.user.User;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        populateAuthors(reviews);
        return reviews;
    }

    public List<Review> getMyReviews() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new org.springframework.security.access.AccessDeniedException("User must be authenticated");
        }
        List<Review> reviews = reviewRepository.findByUserId(currentUser.getId());
        populateAuthors(reviews);
        return reviews;
    }

    public List<Review> getReviewsByPlace(Integer placeId) {
        List<Review> reviews = reviewRepository.findByPlaceId(placeId);
        populateAuthors(reviews);
        return reviews;
    }

    public Review addReview(Integer placeId, Review review) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new org.springframework.security.access.AccessDeniedException("User must be authenticated to add a review");
        }
        review.setPlaceId(placeId);
        review.setUserId(currentUser.getId());
        review.setAuthor(currentUser.getFirstName() + " " + currentUser.getLastName());
        return reviewRepository.save(review);
    }

    private void populateAuthors(List<Review> reviews) {
        for (Review review : reviews) {
            if (review.getUserId() != null && (review.getAuthor() == null || review.getAuthor().trim().isEmpty())) {
                userRepository.findById(review.getUserId()).ifPresent(user -> {
                    review.setAuthor(user.getFirstName() + " " + user.getLastName());
                });
            }
        }
    }
}
