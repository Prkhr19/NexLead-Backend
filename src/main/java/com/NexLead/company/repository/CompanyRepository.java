package com.NexLead.company.repository;

import com.NexLead.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existByCompanyNameIgnoreCase(String companyName);
}
