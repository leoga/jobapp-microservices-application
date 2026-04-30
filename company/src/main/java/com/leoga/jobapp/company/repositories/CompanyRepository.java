package com.leoga.jobapp.company.repositories;

import com.leoga.jobapp.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
