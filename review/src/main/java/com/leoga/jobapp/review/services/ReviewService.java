package com.leoga.jobapp.review.services;

import com.leoga.jobapp.review.dto.ReviewRequest;
import com.leoga.jobapp.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    List<ReviewResponse> getReviews(Long companyId);
    boolean createReview(ReviewRequest reviewRequest);
    ReviewResponse getReviewById(Long reviewId);
    boolean updateReview(ReviewRequest updatedReview, Long reviewId);
    boolean deleteReviewById(Long reviewId);

}
