package com.NexLead.company.controller;

import com.NexLead.company.service.CompanyServiceImpl.CompanyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class Company {
    private final CompanyServiceImpl companyService;

}
