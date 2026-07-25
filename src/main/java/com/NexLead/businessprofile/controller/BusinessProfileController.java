package com.NexLead.businessprofile.controller;

import com.NexLead.businessprofile.dto.BusinessProfileResponseDto;
import com.NexLead.businessprofile.dto.UpdateBusinessProfileRequestDto;
import com.NexLead.businessprofile.service.BusinessProfileService;
import com.NexLead.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/business-profile")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponseDto>> getProfile() {
        BusinessProfileResponseDto profile = businessProfileService.getProfile();
        return ResponseEntity.ok(ApiResponse.success("Business profile fetched successfully", profile));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponseDto>> updateProfile(
            @Valid @RequestBody UpdateBusinessProfileRequestDto request
    ) {
        BusinessProfileResponseDto profile = businessProfileService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Business profile updated successfully", profile));
    }
}
