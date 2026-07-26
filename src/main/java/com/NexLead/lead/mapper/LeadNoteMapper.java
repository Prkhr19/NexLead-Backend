package com.NexLead.lead.mapper;

import com.NexLead.auth.entity.User;
import com.NexLead.lead.dto.LeadNoteResponseDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadNote;
import org.springframework.stereotype.Component;

@Component
public class LeadNoteMapper {

    public LeadNote toEntity(Lead lead, User createdBy, String content) {
        return LeadNote.builder()
                .lead(lead)
                .createdBy(createdBy)
                .content(content)
                .build();
    }

    public LeadNoteResponseDto toResponseDto(LeadNote note) {
        return LeadNoteResponseDto.builder()
                .id(note.getId())
                .leadId(note.getLead().getId())
                .content(note.getContent())
                .createdById(note.getCreatedBy().getId())
                .createdByName(note.getCreatedBy().getName())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
