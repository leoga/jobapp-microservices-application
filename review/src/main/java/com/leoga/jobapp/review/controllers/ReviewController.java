package com.leoga.jobapp.review.controllers;

import com.leoga.jobapp.review.dto.ReviewRequest;
import com.leoga.jobapp.review.dto.ReviewResponse;
import com.leoga.jobapp.review.services.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestParam Long companyId) {
        return ResponseEntity.ok(reviewService.getReviews(companyId));
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody ReviewRequest reviewRequest) {
        if (!reviewService.createReview(reviewRequest)) {
            return ResponseEntity.badRequest().body("Company Not Found");
        }
        return new ResponseEntity<>("Review created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse company = reviewService.getReviewById(reviewId);
        return company != null ? ResponseEntity.ok(company) :  ResponseEntity.notFound().build();
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                               @RequestBody ReviewRequest updatedReview) {
        boolean updated = reviewService.updateReview(updatedReview, reviewId);
        if (updated) return ResponseEntity.ok("Review updated successfully");
        return ResponseEntity.badRequest().body("Company Not Found or Review Not Found");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        return reviewService.deleteReviewById(reviewId) ? ResponseEntity.ok("Review deleted successfully") :  ResponseEntity.notFound().build();
    }

    @GetMapping("/averageRating")
    public Double getAverageReview(@RequestParam Long companyId){
        List<ReviewResponse> reviewList = reviewService.getReviews(companyId);
        return reviewList.stream().mapToDouble(ReviewResponse::getRating).average()
                .orElse(0.0);
    }
}
