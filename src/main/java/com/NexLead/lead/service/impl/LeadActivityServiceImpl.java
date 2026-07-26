package com.NexLead.lead.service.impl;

import com.NexLead.auth.entity.User;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.lead.dto.LeadActivityPageResponseDto;
import com.NexLead.lead.dto.LeadActivityResponseDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivity;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.mapper.LeadActivityMapper;
import com.NexLead.lead.repository.LeadActivityRepository;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadActivityServiceImpl implements LeadActivityService {

    private final LeadRepository leadRepository;
    private final LeadActivityRepository leadActivityRepository;
    private final LeadActivityMapper leadActivityMapper;

    @Override
    @Transactional
    public void recordActivity(Lead lead, LeadActivityType activityType, String description, User performedBy) {
        LeadActivity activity = LeadActivity.builder()
                .lead(lead)
                .activityType(activityType)
                .description(description)
                .performedBy(performedBy)
                .build();
        leadActivityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadActivityPageResponseDto getTimeline(Long leadId, Pageable pageable) {
        if (!leadRepository.existsById(leadId)) {
            throw new ResourceNotFoundException("Lead not found with id: " + leadId);
        }

        Page<LeadActivity> page = leadActivityRepository.findByLeadIdOrderByCreatedAtDesc(leadId, pageable);

        List<LeadActivityResponseDto> content = page.getContent().stream()
                .map(leadActivityMapper::toResponseDto)
                .toList();

        return LeadActivityPageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
