package com.leoga.jobapp.review.mappers;

import com.leoga.jobapp.review.configuration.MapperConfigGlobal;
import com.leoga.jobapp.review.dto.ReviewCreatedEvent;
import com.leoga.jobapp.review.dto.ReviewRequest;
import com.leoga.jobapp.review.dto.ReviewResponse;
import com.leoga.jobapp.review.model.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapperConfigGlobal.class)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewRequest jobRequest);

    ReviewResponse toReviewResponse(Review review);
    ReviewCreatedEvent toReviewCreatedEvent(Review review);

    List<ReviewResponse> toReviewResponseList(List<Review> jobs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchReview(ReviewRequest reviewRequest, @MappingTarget Review job);
}
