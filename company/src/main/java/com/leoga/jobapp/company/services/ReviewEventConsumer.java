package com.leoga.jobapp.company.services;

import com.leoga.jobapp.company.dto.ReviewCreatedEvent;

import java.util.function.Consumer;

public interface ReviewEventConsumer {

    Consumer<ReviewCreatedEvent> reviewCreated();
}
