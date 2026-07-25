package com.NexLead.businessprofile.mapper;

import com.NexLead.businessprofile.dto.BusinessProfileResponseDto;
import com.NexLead.businessprofile.dto.UpdateBusinessProfileRequestDto;
import com.NexLead.businessprofile.entity.BusinessProfile;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

    public BusinessProfileResponseDto toResponseDto(BusinessProfile profile) {
        return BusinessProfileResponseDto.builder()
                .id(profile.getId())
                .companyName(profile.getCompanyName())
                .logo(profile.getLogo())
                .phone(profile.getPhone())
                .email(profile.getEmail())
                .whatsAppNumber(profile.getWhatsAppNumber())
                .address(profile.getAddress())
                .googleMapsUrl(profile.getGoogleMapsUrl())
                .workingHours(profile.getWorkingHours())
                .socialLinks(profile.getSocialLinks())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public void updateEntity(BusinessProfile profile, UpdateBusinessProfileRequestDto request) {
        profile.setCompanyName(request.getCompanyName());
        profile.setLogo(request.getLogo());
        profile.setPhone(request.getPhone());
        profile.setEmail(request.getEmail());
        profile.setWhatsAppNumber(request.getWhatsAppNumber());
        profile.setAddress(request.getAddress());
        profile.setGoogleMapsUrl(request.getGoogleMapsUrl());
        profile.setWorkingHours(request.getWorkingHours());
        profile.setSocialLinks(request.getSocialLinks());
    }

    public BusinessProfile toDefaultEntity() {
        return BusinessProfile.builder()
                .id(BusinessProfile.SINGLETON_ID)
                .companyName("Adore Dezign")
                .build();
    }
}
