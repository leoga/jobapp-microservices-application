package com.leoga.jobapp.company.controllers;

import com.leoga.jobapp.company.dto.CompanyRequest;
import com.leoga.jobapp.company.dto.CompanyResponse;
import com.leoga.jobapp.company.services.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> findAll() {
        return ResponseEntity.ok(companyService.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id, @RequestBody CompanyRequest companyRequest) {
        return companyService.updateCompany(id, companyRequest)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createJob(@RequestBody CompanyRequest companyRequest) {
        return new ResponseEntity<>(companyService.createCompany(companyRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        return companyService.deleteCompanyById(id) ? ResponseEntity.ok("Company deleted successfully") :  ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        CompanyResponse company = companyService.getCompanyById(id);
        return company != null ? ResponseEntity.ok(company) :  ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCompanyRating(@PathVariable Long id) {
        boolean ratingUpdated = companyService.updateCompanyRating(id);
        return ratingUpdated ? ResponseEntity.ok("Company rating updated successfully") :  ResponseEntity.notFound().build();
    }

}
