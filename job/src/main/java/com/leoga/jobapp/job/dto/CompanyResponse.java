package com.leoga.jobapp.job.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private Double rating;
    private List<ReviewResponse> reviews;
}
