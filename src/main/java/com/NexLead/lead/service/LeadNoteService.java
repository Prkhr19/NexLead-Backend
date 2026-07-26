package com.NexLead.lead.service;

import com.NexLead.lead.dto.CreateLeadNoteRequestDto;
import com.NexLead.lead.dto.LeadNotePageResponseDto;
import com.NexLead.lead.dto.LeadNoteResponseDto;
import com.NexLead.lead.dto.UpdateLeadNoteRequestDto;
import org.springframework.data.domain.Pageable;

public interface LeadNoteService {

    LeadNoteResponseDto createNote(Long leadId, CreateLeadNoteRequestDto request);

    LeadNotePageResponseDto getNotesByLeadId(Long leadId, Pageable pageable);

    LeadNoteResponseDto updateNote(Long leadId, Long noteId, UpdateLeadNoteRequestDto request);

    void deleteNote(Long leadId, Long noteId);
}
