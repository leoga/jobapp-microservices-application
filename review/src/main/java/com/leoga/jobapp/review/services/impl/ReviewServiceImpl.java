package com.leoga.jobapp.review.services.impl;

import com.leoga.jobapp.review.clients.CompanyServiceClient;
import com.leoga.jobapp.review.dto.CompanyResponse;
import com.leoga.jobapp.review.dto.ReviewRequest;
import com.leoga.jobapp.review.dto.ReviewResponse;
import com.leoga.jobapp.review.mappers.ReviewMapper;
import com.leoga.jobapp.review.model.Review;
import com.leoga.jobapp.review.repositories.ReviewRepository;
import com.leoga.jobapp.review.services.ReviewService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final CompanyServiceClient companyServiceClient;
    private final StreamBridge streamBridge;
    int attempt = 0;

    @Override
    public List<ReviewResponse> getReviews(Long companyId) {
        return reviewMapper.toReviewResponseList(reviewRepository.findByCompanyId(companyId));
    }

    @Override
    @Retry(name = "retryBreaker", fallbackMethod = "createReviewFallback")
    //@RateLimiter(name = "rateBreaker", fallbackMethod = "createReviewFallback")
    public boolean createReview(ReviewRequest reviewRequest) {

        System.out.println("ATTEMPT COUNT: " + ++attempt);

//        Optional<CompanyResponse> companyOpt = companyServiceClient.getCompanyById(reviewRequest.getCompanyId());
//        if (companyOpt.isEmpty()) {
//            return false;
//        }

        Review savedReview = reviewRepository.save(reviewMapper.toEntity(reviewRequest));
        streamBridge.send("createReview-out-0", reviewMapper.toReviewCreatedEvent(savedReview));
        return true;
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).map(reviewMapper::toReviewResponse).orElse(null);
    }


    @Override
    @CircuitBreaker(name = "companyService", fallbackMethod = "updateReviewFallback")
    public boolean updateReview(ReviewRequest updatedReview, Long reviewId) {

        if (null != updatedReview.getCompanyId()) {
            Optional<CompanyResponse> companyOpt = companyServiceClient.getCompanyById(updatedReview.getCompanyId());
            if (companyOpt.isEmpty()) return false;
        }

        return reviewRepository.findById(reviewId)
                .map(exixtingReview -> {
                    reviewMapper.patchReview(updatedReview, exixtingReview);
                    reviewRepository.save(exixtingReview);
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean deleteReviewById(Long reviewId) {

        return reviewRepository.findById(reviewId)
                .map(review -> {
                    reviewRepository.deleteById(review.getId());
                    return true;
                }).orElse(false);
    }

    public boolean createReviewFallback(ReviewRequest reviewRequest, Exception exception) {
        System.out.println("RETRY/RATE BREAKER FALLBACK CALLED");
        return false;
    }

    public boolean updateReviewFallback(ReviewRequest updatedReview, Long reviewId, Exception e) {
        System.out.println("CIRCUIT BREAKER FALLBACK CALLED");
        return false;
    }
}
