package com.leoga.jobapp.job.clients;

import com.leoga.jobapp.job.dto.ReviewResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/reviews")
public interface ReviewServiceClient {

    @GetExchange
    List<ReviewResponse> getReviews(@RequestParam Long companyId);
}
