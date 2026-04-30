package com.leoga.jobapp.company.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private Double rating;
}
