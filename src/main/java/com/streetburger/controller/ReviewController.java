package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.ReviewRequest;
import com.streetburger.model.Review;
import com.streetburger.repository.ReviewRepository;
import com.streetburger.repository.UserRepository;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Customer reviews management")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all approved reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getAllReviews() {
        List<Review> reviews = reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest 10 reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getLatestReviews() {
        List<Review> reviews = reviewRepository.findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping("/add/{phoneNumber}")
    @Operation(summary = "Add a new review")
    public ResponseEntity<ApiResponse<Review>> addReview(
            @PathVariable String phoneNumber,
            @RequestBody ReviewRequest request) {

        User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

        Review review = new Review();
        review.setUser(user);
        review.setPhoneNumber(phoneNumber);
        review.setReviewerName(request.getReviewerName() != null ? request.getReviewerName()
                : (user != null ? user.getName() : "Anonymous"));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsApproved(true);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    public ResponseEntity<ApiResponse<Review>> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {

        return reviewRepository.findById(id)
                .map(existing -> {
                    // Check if user owns this review
                    if (currentUser != null && existing.getUser() != null
                            && !existing.getUser().getId().equals(currentUser.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.<Review>error("You can only edit your own reviews"));
                    }

                    if (request.getRating() != null)
                        existing.setRating(request.getRating());
                    if (request.getComment() != null)
                        existing.setComment(request.getComment());
                    existing.setUpdatedAt(LocalDateTime.now());

                    reviewRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Review updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Review not found")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        return reviewRepository.findById(id)
                .map(existing -> {
                    // Check if user owns this review or is admin
                    if (currentUser != null && existing.getUser() != null
                            && !existing.getUser().getId().equals(currentUser.getId())
                            && currentUser.getRole() != User.Role.ADMIN) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.<String>error("You can only delete your own reviews"));
                    }

                    reviewRepository.delete(existing);
                    return ResponseEntity.ok(ApiResponse.<String>success("Review deleted", null));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Review not found")));
    }

    @GetMapping("/user/{phoneNumber}")
    @Operation(summary = "Get reviews by user phone number")
    public ResponseEntity<ApiResponse<List<Review>>> getByUser(@PathVariable String phoneNumber) {
        List<Review> reviews = reviewRepository.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
