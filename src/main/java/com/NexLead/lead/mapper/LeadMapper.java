package com.NexLead.lead.mapper;

import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadStatus;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public Lead toEntity(CreateLeadRequestDto request) {
        return Lead.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .city(request.getCity())
                .propertyType(request.getPropertyType())
                .serviceInterested(request.getServiceInterested())
                .budget(request.getBudget())
                .projectStage(request.getProjectStage())
                .notes(request.getNotes())
                .status(LeadStatus.NEW)
                .build();
    }

    public void updateEntity(Lead lead, UpdateLeadRequestDto request) {
        lead.setName(request.getName());
        lead.setPhone(request.getPhone());
        lead.setEmail(request.getEmail());
        lead.setCity(request.getCity());
        lead.setPropertyType(request.getPropertyType());
        lead.setServiceInterested(request.getServiceInterested());
        lead.setBudget(request.getBudget());
        lead.setProjectStage(request.getProjectStage());
        lead.setNotes(request.getNotes());
        lead.setStatus(request.getStatus());
    }

    public LeadResponseDto toResponseDto(Lead lead) {
        return LeadResponseDto.builder()
                .id(lead.getId())
                .name(lead.getName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .city(lead.getCity())
                .propertyType(lead.getPropertyType())
                .serviceInterested(lead.getServiceInterested())
                .budget(lead.getBudget())
                .projectStage(lead.getProjectStage())
                .notes(lead.getNotes())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}
