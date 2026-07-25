package com.NexLead.lead.service.impl;

import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadPageResponseDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import com.NexLead.lead.mapper.LeadMapper;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadService;
import com.NexLead.lead.specification.LeadSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    @Override
    @Transactional
    public LeadResponseDto createLead(CreateLeadRequestDto request) {
        Lead lead = leadMapper.toEntity(request);
        Lead saved = leadRepository.save(lead);
        return leadMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponseDto getLeadById(Long id) {
        Lead lead = findLeadOrThrow(id);
        return leadMapper.toResponseDto(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadPageResponseDto getLeads(
            LeadStatus status,
            String city,
            ServiceInterested serviceInterested,
            String search,
            Pageable pageable
    ) {
        Specification<Lead> specification = LeadSpecification.build(status, city, serviceInterested, search);
        Page<Lead> page = leadRepository.findAll(specification, pageable);

        List<LeadResponseDto> content = page.getContent().stream()
                .map(leadMapper::toResponseDto)
                .toList();

        return LeadPageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public LeadResponseDto updateLead(Long id, UpdateLeadRequestDto request) {
        Lead lead = findLeadOrThrow(id);
        leadMapper.updateEntity(lead, request);
        Lead saved = leadRepository.save(lead);
        return leadMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        Lead lead = findLeadOrThrow(id);
        leadRepository.delete(lead);
    }

    private Lead findLeadOrThrow(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }
}
