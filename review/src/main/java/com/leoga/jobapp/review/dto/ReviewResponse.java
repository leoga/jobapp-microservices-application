package com.leoga.jobapp.review.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long companyId;
    private String title;
    private String description;
    private Double rating;
}
