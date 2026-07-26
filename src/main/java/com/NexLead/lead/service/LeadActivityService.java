package com.NexLead.lead.service;

import com.NexLead.auth.entity.User;
import com.NexLead.lead.dto.LeadActivityPageResponseDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivityType;
import org.springframework.data.domain.Pageable;

public interface LeadActivityService {

    void recordActivity(Lead lead, LeadActivityType activityType, String description, User performedBy);

    LeadActivityPageResponseDto getTimeline(Long leadId, Pageable pageable);
}
