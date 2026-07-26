package com.NexLead.lead.service.impl;

import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.common.util.SecurityUtils;
import com.NexLead.lead.dto.AssignLeadRequestDto;
import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadPageResponseDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import com.NexLead.lead.mapper.LeadMapper;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import com.NexLead.lead.service.LeadService;
import com.NexLead.lead.specification.LeadSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;
    private final LeadActivityService leadActivityService;

    @Override
    @Transactional
    public LeadResponseDto createLead(CreateLeadRequestDto request) {
        User assignee = findAssigneeIfPresent(request.getAssignedToId());
        Lead lead = leadMapper.toEntity(request, assignee);
        Lead saved = leadRepository.save(lead);

        User currentUser = findCurrentUserOrNull();
        leadActivityService.recordActivity(
                saved,
                LeadActivityType.LEAD_CREATED,
                "Lead created: " + saved.getName(),
                currentUser
        );

        if (assignee != null) {
            leadActivityService.recordActivity(
                    saved,
                    LeadActivityType.LEAD_ASSIGNED,
                    "Lead assigned to " + assignee.getName(),
                    currentUser
            );
        }

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
            Long assignedToId,
            boolean assignedToMe,
            Pageable pageable
    ) {
        Long effectiveAssigneeId = assignedToId;
        if (assignedToMe) {
            effectiveAssigneeId = findCurrentUser().getId();
        }

        Specification<Lead> specification = LeadSpecification.build(
                status, city, serviceInterested, search, effectiveAssigneeId
        );
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
        LeadStatus previousStatus = lead.getStatus();
        leadMapper.updateEntity(lead, request);
        Lead saved = leadRepository.save(lead);

        if (previousStatus != saved.getStatus()) {
            leadActivityService.recordActivity(
                    saved,
                    LeadActivityType.STATUS_CHANGED,
                    "Status changed from " + previousStatus + " to " + saved.getStatus(),
                    findCurrentUserOrNull()
            );
        }

        return leadMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public LeadResponseDto assignLead(Long id, AssignLeadRequestDto request) {
        Lead lead = findLeadOrThrow(id);
        User previousAssignee = lead.getAssignedTo();
        User assignee = findAssigneeIfPresent(request.getAssignedToId());
        lead.setAssignedTo(assignee);
        Lead saved = leadRepository.save(lead);

        User currentUser = findCurrentUserOrNull();
        if (assignee == null && previousAssignee != null) {
            leadActivityService.recordActivity(
                    saved,
                    LeadActivityType.LEAD_UNASSIGNED,
                    "Lead unassigned from " + previousAssignee.getName(),
                    currentUser
            );
        } else if (assignee != null && !Objects.equals(previousAssignee != null ? previousAssignee.getId() : null, assignee.getId())) {
            leadActivityService.recordActivity(
                    saved,
                    LeadActivityType.LEAD_ASSIGNED,
                    "Lead assigned to " + assignee.getName(),
                    currentUser
            );
        }

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

    private User findAssigneeIfPresent(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id: " + assigneeId));
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private User findCurrentUserOrNull() {
        try {
            return findCurrentUser();
        } catch (IllegalStateException ex) {
            return null;
        }
    }
}
