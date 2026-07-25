package com.NexLead.businessprofile.service.impl;

import com.NexLead.businessprofile.dto.BusinessProfileResponseDto;
import com.NexLead.businessprofile.dto.UpdateBusinessProfileRequestDto;
import com.NexLead.businessprofile.entity.BusinessProfile;
import com.NexLead.businessprofile.mapper.BusinessProfileMapper;
import com.NexLead.businessprofile.repository.BusinessProfileRepository;
import com.NexLead.businessprofile.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessProfileServiceImpl implements BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;
    private final BusinessProfileMapper businessProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileResponseDto getProfile() {
        BusinessProfile profile = businessProfileRepository.findById(BusinessProfile.SINGLETON_ID)
                .orElseGet(this::createDefaultProfile);
        return businessProfileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public BusinessProfileResponseDto updateProfile(UpdateBusinessProfileRequestDto request) {
        BusinessProfile profile = businessProfileRepository.findById(BusinessProfile.SINGLETON_ID)
                .orElseGet(this::createDefaultProfile);

        businessProfileMapper.updateEntity(profile, request);
        BusinessProfile saved = businessProfileRepository.save(profile);
        return businessProfileMapper.toResponseDto(saved);
    }

    private BusinessProfile createDefaultProfile() {
        return businessProfileRepository.save(businessProfileMapper.toDefaultEntity());
    }
}
