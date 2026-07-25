package com.NexLead.lead.service;

import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadPageResponseDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import org.springframework.data.domain.Pageable;

public interface LeadService {

    LeadResponseDto createLead(CreateLeadRequestDto request);

    LeadResponseDto getLeadById(Long id);

    LeadPageResponseDto getLeads(
            LeadStatus status,
            String city,
            ServiceInterested serviceInterested,
            String search,
            Pageable pageable
    );

    LeadResponseDto updateLead(Long id, UpdateLeadRequestDto request);

    void deleteLead(Long id);
}
