package com.NexLead.lead.mapper;

import com.NexLead.lead.dto.LeadActivityResponseDto;
import com.NexLead.lead.entity.LeadActivity;
import org.springframework.stereotype.Component;

@Component
public class LeadActivityMapper {

    public LeadActivityResponseDto toResponseDto(LeadActivity activity) {
        return LeadActivityResponseDto.builder()
                .id(activity.getId())
                .leadId(activity.getLead().getId())
                .activityType(activity.getActivityType())
                .description(activity.getDescription())
                .performedById(activity.getPerformedBy() != null ? activity.getPerformedBy().getId() : null)
                .performedByName(activity.getPerformedBy() != null ? activity.getPerformedBy().getName() : null)
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
