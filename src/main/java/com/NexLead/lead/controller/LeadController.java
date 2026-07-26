package com.NexLead.lead.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.lead.dto.AssignLeadRequestDto;
import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadPageResponseDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import com.NexLead.lead.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponseDto>> createLead(
            @Valid @RequestBody CreateLeadRequestDto request
    ) {
        LeadResponseDto lead = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lead created successfully", lead));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> getLeadById(@PathVariable Long id) {
        LeadResponseDto lead = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.success("Lead fetched successfully", lead));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LeadPageResponseDto>> getLeads(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ServiceInterested serviceInterested,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(defaultValue = "false") boolean assignedToMe,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LeadPageResponseDto leads = leadService.getLeads(
                status, city, serviceInterested, search, assignedToId, assignedToMe, pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Leads fetched successfully", leads));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadRequestDto request
    ) {
        LeadResponseDto lead = leadService.updateLead(id, request);
        return ResponseEntity.ok(ApiResponse.success("Lead updated successfully", lead));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<LeadResponseDto>> assignLead(
            @PathVariable Long id,
            @RequestBody AssignLeadRequestDto request
    ) {
        LeadResponseDto lead = leadService.assignLead(id, request);
        return ResponseEntity.ok(ApiResponse.success("Lead assignment updated successfully", lead));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(ApiResponse.success("Lead deleted successfully", null));
    }
}
