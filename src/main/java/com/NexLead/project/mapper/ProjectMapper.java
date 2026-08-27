package com.NexLead.project.mapper;

import com.NexLead.lead.entity.Lead;
import com.NexLead.project.dto.ConvertLeadToProjectRequestDto;
import com.NexLead.project.dto.ProjectResponseDto;
import com.NexLead.project.dto.UpdateProjectRequestDto;
import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntityFromLead(Lead lead, ConvertLeadToProjectRequestDto request) {
        String projectName = request.getName();
        if (projectName == null || projectName.isBlank()) {
            projectName = lead.getName();
        }

        return Project.builder()
                .name(projectName)
                .lead(lead)
                .customerName(lead.getName())
                .customerPhone(lead.getPhone())
                .customerEmail(lead.getEmail())
                .city(lead.getCity())
                .address(request.getAddress())
                .serviceInterested(lead.getServiceInterested())
                .status(ProjectStatus.CREATED)
                .contractAmount(request.getContractAmount())
                .startDate(request.getStartDate())
                .expectedCompletionDate(request.getExpectedCompletionDate())
                .notes(request.getNotes())
                .build();
    }

    public ProjectResponseDto toResponseDto(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .leadId(project.getLead().getId())
                .leadName(project.getLead().getName())
                .customerName(project.getCustomerName())
                .customerPhone(project.getCustomerPhone())
                .customerEmail(project.getCustomerEmail())
                .city(project.getCity())
                .address(project.getAddress())
                .serviceInterested(project.getServiceInterested())
                .status(project.getStatus())
                .contractAmount(project.getContractAmount())
                .startDate(project.getStartDate())
                .expectedCompletionDate(project.getExpectedCompletionDate())
                .actualCompletionDate(project.getActualCompletionDate())
                .notes(project.getNotes())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public void updateEntity(Project project, UpdateProjectRequestDto request) {
        project.setName(request.getName());
        project.setCustomerName(request.getCustomerName());
        project.setCustomerPhone(request.getCustomerPhone());
        project.setCustomerEmail(request.getCustomerEmail());
        project.setCity(request.getCity());
        project.setAddress(request.getAddress());
        project.setServiceInterested(request.getServiceInterested());
        project.setStatus(request.getStatus());
        project.setContractAmount(request.getContractAmount());
        project.setStartDate(request.getStartDate());
        project.setExpectedCompletionDate(request.getExpectedCompletionDate());
        project.setActualCompletionDate(request.getActualCompletionDate());
        project.setNotes(request.getNotes());
    }
}
