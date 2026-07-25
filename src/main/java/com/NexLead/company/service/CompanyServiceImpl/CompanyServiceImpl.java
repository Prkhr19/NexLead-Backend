package com.NexLead.company.service.CompanyServiceImpl;

import com.NexLead.company.repository.CompanyRepository;
import com.NexLead.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    }

