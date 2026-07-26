package com.NexLead.lead.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.lead.dto.CreateLeadNoteRequestDto;
import com.NexLead.lead.dto.LeadNotePageResponseDto;
import com.NexLead.lead.dto.LeadNoteResponseDto;
import com.NexLead.lead.dto.UpdateLeadNoteRequestDto;
import com.NexLead.lead.service.LeadNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leads/{leadId}/notes")
public class LeadNoteController {

    private final LeadNoteService leadNoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<LeadNoteResponseDto>> createNote(
            @PathVariable Long leadId,
            @Valid @RequestBody CreateLeadNoteRequestDto request
    ) {
        LeadNoteResponseDto note = leadNoteService.createNote(leadId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lead note created successfully", note));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LeadNotePageResponseDto>> getNotes(
            @PathVariable Long leadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LeadNotePageResponseDto notes = leadNoteService.getNotesByLeadId(leadId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lead notes fetched successfully", notes));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse<LeadNoteResponseDto>> updateNote(
            @PathVariable Long leadId,
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateLeadNoteRequestDto request
    ) {
        LeadNoteResponseDto note = leadNoteService.updateNote(leadId, noteId, request);
        return ResponseEntity.ok(ApiResponse.success("Lead note updated successfully", note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @PathVariable Long leadId,
            @PathVariable Long noteId
    ) {
        leadNoteService.deleteNote(leadId, noteId);
        return ResponseEntity.ok(ApiResponse.success("Lead note deleted successfully", null));
    }
}
