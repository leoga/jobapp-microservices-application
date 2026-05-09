package com.leoga.jobapp.job.clients;

import com.leoga.jobapp.job.dto.CompanyResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;


@HttpExchange("/companies")
public interface CompanyServiceClient {

    @GetExchange("/{id}")
    Optional<CompanyResponse> getCompanyById(@PathVariable Long id);
}
