package com.NexLead.followup.service;

import com.NexLead.followup.dto.CreateFollowUpRequestDto;
import com.NexLead.followup.dto.FollowUpPageResponseDto;
import com.NexLead.followup.dto.FollowUpResponseDto;
import com.NexLead.followup.dto.UpdateFollowUpRequestDto;
import org.springframework.data.domain.Pageable;

public interface FollowUpService {

    FollowUpResponseDto createFollowUp(CreateFollowUpRequestDto request);

    FollowUpResponseDto getFollowUpById(Long id);

    FollowUpPageResponseDto getFollowUps(
            Long leadId,
            Long assignedToId,
            boolean assignedToMe,
            Boolean completed,
            Boolean overdue,
            Boolean dueToday,
            Pageable pageable
    );

    FollowUpResponseDto updateFollowUp(Long id, UpdateFollowUpRequestDto request);

    FollowUpResponseDto completeFollowUp(Long id);

    void deleteFollowUp(Long id);
}
