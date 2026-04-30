package com.leoga.jobapp.company.clients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange("/reviews")
public interface ReviewServiceClient {

    @GetExchange("/averageRating")
    Double getAverageReview(@RequestParam Long companyId);
}
