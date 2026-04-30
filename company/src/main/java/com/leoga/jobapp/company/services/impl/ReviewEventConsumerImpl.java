package com.leoga.jobapp.company.services.impl;

import com.leoga.jobapp.company.dto.ReviewCreatedEvent;
import com.leoga.jobapp.company.services.CompanyService;
import com.leoga.jobapp.company.services.ReviewEventConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ReviewEventConsumerImpl implements ReviewEventConsumer {

    private final CompanyService companyService;

    @Override
    @Bean
    public Consumer<ReviewCreatedEvent> reviewCreated() {
        return companyService::updateCompanyRating;
    }
}
