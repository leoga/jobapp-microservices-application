package com.leoga.jobapp.company.services;

import com.leoga.jobapp.company.dto.CompanyRequest;
import com.leoga.jobapp.company.dto.CompanyResponse;
import com.leoga.jobapp.company.dto.ReviewCreatedEvent;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    List<CompanyResponse> findAll();
    CompanyResponse createCompany(CompanyRequest companyRequest);
    Optional<CompanyResponse> updateCompany(Long id, CompanyRequest companyRequest);
    boolean deleteCompanyById(Long id);
    CompanyResponse getCompanyById(Long id);
    void updateCompanyRating(ReviewCreatedEvent reviewCreatedEvent);
    boolean updateCompanyRating(Long id);
}
