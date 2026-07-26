package com.NexLead.lead.service.impl;

import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.common.util.SecurityUtils;
import com.NexLead.lead.dto.CreateLeadNoteRequestDto;
import com.NexLead.lead.dto.LeadNotePageResponseDto;
import com.NexLead.lead.dto.LeadNoteResponseDto;
import com.NexLead.lead.dto.UpdateLeadNoteRequestDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadNote;
import com.NexLead.lead.mapper.LeadNoteMapper;
import com.NexLead.lead.repository.LeadNoteRepository;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadNoteServiceImpl implements LeadNoteService {

    private final LeadRepository leadRepository;
    private final LeadNoteRepository leadNoteRepository;
    private final UserRepository userRepository;
    private final LeadNoteMapper leadNoteMapper;

    @Override
    @Transactional
    public LeadNoteResponseDto createNote(Long leadId, CreateLeadNoteRequestDto request) {
        Lead lead = findLeadOrThrow(leadId);
        User currentUser = findCurrentUser();

        LeadNote note = leadNoteMapper.toEntity(lead, currentUser, request.getContent());
        LeadNote saved = leadNoteRepository.save(note);
        return leadNoteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadNotePageResponseDto getNotesByLeadId(Long leadId, Pageable pageable) {
        findLeadOrThrow(leadId);
        Page<LeadNote> page = leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(leadId, pageable);

        List<LeadNoteResponseDto> content = page.getContent().stream()
                .map(leadNoteMapper::toResponseDto)
                .toList();

        return LeadNotePageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public LeadNoteResponseDto updateNote(Long leadId, Long noteId, UpdateLeadNoteRequestDto request) {
        LeadNote note = findNoteOrThrow(leadId, noteId);
        note.setContent(request.getContent());
        LeadNote saved = leadNoteRepository.save(note);
        return leadNoteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteNote(Long leadId, Long noteId) {
        LeadNote note = findNoteOrThrow(leadId, noteId);
        leadNoteRepository.delete(note);
    }

    private Lead findLeadOrThrow(Long leadId) {
        return leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
    }

    private LeadNote findNoteOrThrow(Long leadId, Long noteId) {
        return leadNoteRepository.findByIdAndLeadId(noteId, leadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId + " for lead: " + leadId));
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
