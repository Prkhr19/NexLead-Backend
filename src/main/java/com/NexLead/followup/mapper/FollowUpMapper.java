package com.NexLead.followup.mapper;

import com.NexLead.followup.dto.CreateFollowUpRequestDto;
import com.NexLead.followup.dto.FollowUpResponseDto;
import com.NexLead.followup.dto.UpdateFollowUpRequestDto;
import com.NexLead.followup.entity.FollowUp;
import com.NexLead.auth.entity.User;
import com.NexLead.lead.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class FollowUpMapper {

    public FollowUp toEntity(CreateFollowUpRequestDto request, Lead lead, User assignedTo, User createdBy) {
        return FollowUp.builder()
                .lead(lead)
                .assignedTo(assignedTo)
                .createdBy(createdBy)
                .followUpType(request.getFollowUpType())
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .completed(false)
                .build();
    }

    public void updateEntity(FollowUp followUp, UpdateFollowUpRequestDto request, User assignedTo) {
        followUp.setAssignedTo(assignedTo);
        followUp.setFollowUpType(request.getFollowUpType());
        followUp.setDueDate(request.getDueDate());
        followUp.setNotes(request.getNotes());
    }

    public FollowUpResponseDto toResponseDto(FollowUp followUp) {
        return FollowUpResponseDto.builder()
                .id(followUp.getId())
                .leadId(followUp.getLead().getId())
                .leadName(followUp.getLead().getName())
                .assignedToId(followUp.getAssignedTo().getId())
                .assignedToName(followUp.getAssignedTo().getName())
                .createdById(followUp.getCreatedBy().getId())
                .createdByName(followUp.getCreatedBy().getName())
                .followUpType(followUp.getFollowUpType())
                .dueDate(followUp.getDueDate())
                .notes(followUp.getNotes())
                .completed(followUp.isCompleted())
                .createdAt(followUp.getCreatedAt())
                .updatedAt(followUp.getUpdatedAt())
                .build();
    }
}
