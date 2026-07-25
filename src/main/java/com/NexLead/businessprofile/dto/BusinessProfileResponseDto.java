package com.NexLead.businessprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfileResponseDto {

    private Long id;
    private String companyName;
    private String logo;
    private String phone;
    private String email;
    private String whatsAppNumber;
    private String address;
    private String googleMapsUrl;
    private String workingHours;
    private String socialLinks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
