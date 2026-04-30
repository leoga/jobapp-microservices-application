package com.leoga.jobapp.company.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CompanyRequest {
    private String name;
    private String description;
}
