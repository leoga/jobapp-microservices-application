package com.leoga.jobapp.company.services.impl;

import com.leoga.jobapp.company.clients.ReviewServiceClient;
import com.leoga.jobapp.company.dto.CompanyRequest;
import com.leoga.jobapp.company.dto.CompanyResponse;
import com.leoga.jobapp.company.dto.ReviewCreatedEvent;
import com.leoga.jobapp.company.mappers.CompanyMapper;
import com.leoga.jobapp.company.model.Company;
import com.leoga.jobapp.company.repositories.CompanyRepository;
import com.leoga.jobapp.company.services.CompanyService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ReviewServiceClient reviewServiceClient;
    private final CompanyMapper companyMapper;

    @Override
    public List<CompanyResponse> findAll() {
        return companyMapper.toCompanyResponseList(companyRepository.findAll());
    }

    @Override
    public CompanyResponse createCompany(CompanyRequest companyRequest) {
        Company savedCompany = companyRepository.save(companyMapper.toEntity(companyRequest));
        return companyMapper.toCompanyResponse(savedCompany);
    }

    @Override
    public Optional<CompanyResponse> updateCompany(Long id, CompanyRequest updatedCompany) {
        return companyRepository.findById(id)
                .map(exixtingCompany -> {
                    companyMapper.patchCompany(updatedCompany, exixtingCompany);
                    Company savedJob = companyRepository.save(exixtingCompany);
                    return companyMapper.toCompanyResponse(savedJob);
                });
    }

    @Override
    public boolean deleteCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyRepository.deleteById(company.getId());
                    return true;
                }).orElse(false);
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        return companyRepository.findById(id).map(companyMapper::toCompanyResponse).orElse(null);
    }

    @Override
    public void updateCompanyRating(ReviewCreatedEvent reviewCreatedEvent) {
        Company company = companyRepository.findById(reviewCreatedEvent.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Company not found" + reviewCreatedEvent.getCompanyId()));

        Double averageRating = reviewServiceClient.getAverageReview(reviewCreatedEvent.getCompanyId());
        company.setRating(Math.round(averageRating * 100.0) / 100.0);
        companyRepository.save(company);
    }

    @Override
    public boolean updateCompanyRating(Long id) {
        Company company = companyRepository.findById(id).orElse(null);
        if (null == company) return false;

        Double averageRating = reviewServiceClient.getAverageReview(id);
        company.setRating(Math.round(averageRating * 100.0) / 100.0);
        companyRepository.save(company);
        return true;
    }
}
