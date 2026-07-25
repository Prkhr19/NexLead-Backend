package com.NexLead.company.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyResponseDto {
    private Long id;
    private String companyName;
    private String email;
    private String location;
    private String website;
    private String phoneNumber;
    private String industry;
    private LocalDateTime createdAt;

}
