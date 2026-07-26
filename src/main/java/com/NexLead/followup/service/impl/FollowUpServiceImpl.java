package com.NexLead.followup.service.impl;

import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.common.util.SecurityUtils;
import com.NexLead.followup.dto.CreateFollowUpRequestDto;
import com.NexLead.followup.dto.FollowUpPageResponseDto;
import com.NexLead.followup.dto.FollowUpResponseDto;
import com.NexLead.followup.dto.UpdateFollowUpRequestDto;
import com.NexLead.followup.entity.FollowUp;
import com.NexLead.followup.mapper.FollowUpMapper;
import com.NexLead.followup.repository.FollowUpRepository;
import com.NexLead.followup.service.FollowUpService;
import com.NexLead.followup.specification.FollowUpSpecification;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowUpServiceImpl implements FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final FollowUpMapper followUpMapper;
    private final LeadActivityService leadActivityService;

    @Override
    @Transactional
    public FollowUpResponseDto createFollowUp(CreateFollowUpRequestDto request) {
        Lead lead = findLeadOrThrow(request.getLeadId());
        User assignedTo = findUserOrThrow(request.getAssignedToId());
        User currentUser = findCurrentUser();

        FollowUp followUp = followUpMapper.toEntity(request, lead, assignedTo, currentUser);
        FollowUp saved = followUpRepository.save(followUp);

        leadActivityService.recordActivity(
                lead,
                LeadActivityType.FOLLOW_UP_CREATED,
                "Follow-up scheduled for " + saved.getDueDate() + " (" + saved.getFollowUpType() + ")",
                currentUser
        );

        return followUpMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowUpResponseDto getFollowUpById(Long id) {
        return followUpMapper.toResponseDto(findFollowUpOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FollowUpPageResponseDto getFollowUps(
            Long leadId,
            Long assignedToId,
            boolean assignedToMe,
            Boolean completed,
            Boolean overdue,
            Boolean dueToday,
            Pageable pageable
    ) {
        Long effectiveAssigneeId = assignedToId;
        if (assignedToMe) {
            effectiveAssigneeId = findCurrentUser().getId();
        }

        LocalDate today = LocalDate.now();
        Specification<FollowUp> specification = FollowUpSpecification.build(
                leadId, effectiveAssigneeId, completed, overdue, dueToday, today
        );
        Page<FollowUp> page = followUpRepository.findAll(specification, pageable);

        List<FollowUpResponseDto> content = page.getContent().stream()
                .map(followUpMapper::toResponseDto)
                .toList();

        return FollowUpPageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public FollowUpResponseDto updateFollowUp(Long id, UpdateFollowUpRequestDto request) {
        FollowUp followUp = findFollowUpOrThrow(id);
        if (followUp.isCompleted()) {
            throw new BusinessRuleException("Completed follow-ups cannot be updated");
        }
        User assignedTo = findUserOrThrow(request.getAssignedToId());
        followUpMapper.updateEntity(followUp, request, assignedTo);
        FollowUp saved = followUpRepository.save(followUp);
        return followUpMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public FollowUpResponseDto completeFollowUp(Long id) {
        FollowUp followUp = findFollowUpOrThrow(id);
        if (followUp.isCompleted()) {
            throw new BusinessRuleException("Follow-up is already completed");
        }
        followUp.setCompleted(true);
        FollowUp saved = followUpRepository.save(followUp);

        leadActivityService.recordActivity(
                saved.getLead(),
                LeadActivityType.FOLLOW_UP_COMPLETED,
                "Follow-up completed (" + saved.getFollowUpType() + ")",
                findCurrentUser()
        );

        return followUpMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteFollowUp(Long id) {
        FollowUp followUp = findFollowUpOrThrow(id);
        followUpRepository.delete(followUp);
    }

    private FollowUp findFollowUpOrThrow(Long id) {
        return followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found with id: " + id));
    }

    private Lead findLeadOrThrow(Long leadId) {
        return leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
