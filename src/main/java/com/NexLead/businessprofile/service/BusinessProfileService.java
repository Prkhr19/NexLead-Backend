package com.NexLead.businessprofile.service;

import com.NexLead.businessprofile.dto.BusinessProfileResponseDto;
import com.NexLead.businessprofile.dto.UpdateBusinessProfileRequestDto;

public interface BusinessProfileService {

    BusinessProfileResponseDto getProfile();

    BusinessProfileResponseDto updateProfile(UpdateBusinessProfileRequestDto request);
}
