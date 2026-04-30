package com.leoga.jobapp.company.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewCreatedEvent {
    private Long id;
    private String title;
    private String description;
    private double rating;
    private Long companyId;
}
